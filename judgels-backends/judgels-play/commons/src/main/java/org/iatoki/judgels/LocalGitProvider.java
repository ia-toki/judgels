package org.iatoki.judgels;

import com.google.common.collect.ImmutableList;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.IllegalTodoFileModification;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RebaseTodoLine;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public final class LocalGitProvider implements GitProvider {

    private final LocalFileSystemProvider fileSystemProvider;

    public LocalGitProvider(LocalFileSystemProvider fileSystemProvider) {
        this.fileSystemProvider = fileSystemProvider;
    }

    @Override
    public void init(List<String> rootDirPath) {
        File dir = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Git.init().setDirectory(dir).call().close();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clone(List<String> originDirPath, List<String> rootDirPath) {
        String uri = "file://" + fileSystemProvider.getURL(originDirPath);

        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Git.cloneRepository().setURI(uri).setDirectory(root).call().close();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean fetch(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new Git(repo).fetch().setCheckFetchedObjects(true).call();

            RevWalk walk = new RevWalk(repo);
            RevCommit commit = walk.parseCommit(repo.getRef("master").getObjectId());

            walk.reset();
            RevCommit originCommit = walk.parseCommit(repo.getRef("origin/master").getObjectId());

            repo.close();
            return !commit.getName().equals(originCommit.getName());
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAll(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));
        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new Git(repo).add().addFilepattern(".").call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit(List<String> rootDirPath, String committerName, String committerEmail, String title, String description) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new Git(repo).commit().setAuthor(committerName, committerEmail).setMessage(title + "\n\n" + description).call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean rebase(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            RebaseResult result = new Git(repo).rebase().setUpstream("origin/master").call();

            if (result.getStatus() == RebaseResult.Status.STOPPED) {
                new Git(repo).rebase().setOperation(RebaseCommand.Operation.ABORT).call();
                repo.close();
                return false;
            }
            repo.close();
            return true;

        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean push(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new Git(repo).push().setRefSpecs(new RefSpec("master:master")).call();
            repo.close();
            return true;
        } catch (TransportException e) {
            return false;
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void resetToParent(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new Git(repo).reset().setRef(repo.resolve("HEAD^").getName()).call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetHard(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new Git(repo).reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GitCommit> getLog(List<String> rootDirPath) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));

            Iterable<RevCommit> logs = new Git(repo).log().call();
            ImmutableList.Builder<GitCommit> versions = ImmutableList.builder();
            for (RevCommit rev : logs) {
                versions.add(new GitCommit(rev.getName(), rev.getAuthorIdent().getName(), new Date(rev.getCommitTime() * 1000L), rev.getShortMessage(), rev.getFullMessage()));
            }
            repo.close();
            return versions.build();

        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restore(List<String> rootDirPath, String hash) {
        File root = new File(fileSystemProvider.getURL(rootDirPath));

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));

            ObjectId head = repo.resolve("HEAD");

            RevertCommand command = new Git(repo).revert();
            Iterable<RevCommit> logs = new Git(repo).log().call();
            for (RevCommit rev : logs) {
                if (rev.getName().equals(hash)) {
                    break;
                }
                command.include(rev);
            }
            command.call();

            new Git(repo).rebase().setUpstream(head).runInteractively(new RebaseCommand.InteractiveHandler() {
                @Override
                public void prepareSteps(List<RebaseTodoLine> list) {
                    for (int i = 0; i < list.size(); i++) {
                        try {
                            if (i == 0) {
                                list.get(i).setAction(RebaseTodoLine.Action.REWORD);
                            } else {
                                list.get(i).setAction(RebaseTodoLine.Action.FIXUP);
                            }
                        } catch (IllegalTodoFileModification e) {
                            // nothing
                        }
                    }
                }

                @Override
                public String modifyCommitMessage(String s) {
                    return "Revert to commit " + hash.substring(0, 7);
                }
            }).call();

            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getCommitDiff(List<String> rootDirPath, String commitHash) {
        return "";
    }
}
