package judgels.sandalphon;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import judgels.fs.local.LocalFileSystem;
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

public final class LocalGit implements Git {

    private final LocalFileSystem fs;

    public LocalGit(LocalFileSystem fs) {
        this.fs = fs;
    }

    @Override
    public void init(Path rootDirPath) {
        File dir = fs.getFile(rootDirPath);

        try {
            org.eclipse.jgit.api.Git.init().setInitialBranch("master").setDirectory(dir).call().close();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clone(Path originDirPath, Path rootDirPath) {
        String uri = "file://" + fs.getFile(originDirPath).getAbsolutePath();

        File root = fs.getFile(rootDirPath);

        try {
            org.eclipse.jgit.api.Git.cloneRepository().setURI(uri).setDirectory(root).call().close();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean fetch(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new org.eclipse.jgit.api.Git(repo).fetch().setCheckFetchedObjects(true).call();

            RevWalk walk = new RevWalk(repo);
            RevCommit commit = walk.parseCommit(repo.resolve("master").toObjectId());

            walk.reset();
            RevCommit originCommit = walk.parseCommit(repo.resolve("origin/master").toObjectId());

            repo.close();
            return !commit.getName().equals(originCommit.getName());
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addAll(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);
        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new org.eclipse.jgit.api.Git(repo).add().addFilepattern(".").call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit(Path rootDirPath, String committerName, String committerEmail, String title, String description) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new org.eclipse.jgit.api.Git(repo).commit().setAuthor(committerName, committerEmail).setMessage(title + "\n\n" + description).call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean rebase(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            RebaseResult result = new org.eclipse.jgit.api.Git(repo).rebase().setUpstream("origin/master").call();

            if (result.getStatus() == RebaseResult.Status.STOPPED) {
                new org.eclipse.jgit.api.Git(repo).rebase().setOperation(RebaseCommand.Operation.ABORT).call();
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
    public boolean push(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new org.eclipse.jgit.api.Git(repo).push().setRefSpecs(new RefSpec("master:master")).call();
            repo.close();
            return true;
        } catch (TransportException e) {
            return false;
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void resetToParent(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new org.eclipse.jgit.api.Git(repo).reset().setRef(repo.resolve("HEAD^").getName()).call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetHard(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));
            new org.eclipse.jgit.api.Git(repo).reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call();
            repo.close();
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GitCommit> getLog(Path rootDirPath) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));

            Iterable<RevCommit> logs = new org.eclipse.jgit.api.Git(repo).log().call();
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
    public void restore(Path rootDirPath, String hash) {
        File root = fs.getFile(rootDirPath);

        try {
            Repository repo = FileRepositoryBuilder.create(new File(root, ".git"));

            ObjectId head = repo.resolve("HEAD");

            RevertCommand command = new org.eclipse.jgit.api.Git(repo).revert();
            Iterable<RevCommit> logs = new org.eclipse.jgit.api.Git(repo).log().call();
            for (RevCommit rev : logs) {
                if (rev.getName().equals(hash)) {
                    break;
                }
                command.include(rev);
            }
            command.call();

            new org.eclipse.jgit.api.Git(repo).rebase().setUpstream(head).runInteractively(new RebaseCommand.InteractiveHandler() {
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
}
