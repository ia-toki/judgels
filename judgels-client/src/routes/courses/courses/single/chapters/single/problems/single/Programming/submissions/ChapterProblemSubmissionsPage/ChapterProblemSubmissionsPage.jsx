import { Switch } from '@blueprintjs/core';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import PaginationV2 from '../../../../../../../../../../../components/PaginationV2/PaginationV2';
import { RegradeAllButton } from '../../../../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import {
  chapterProgrammingSubmissionsQueryOptions,
  regradeChapterProgrammingSubmissionMutationOptions,
  regradeChapterProgrammingSubmissionsMutationOptions,
} from '../../../../../../../../../../../modules/queries/chapterSubmissionProgramming';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../../modules/queries/course';
import { useSession } from '../../../../../../../../../../../modules/session';
import { reallyConfirm } from '../../../../../../../../../../../utils/confirmation';
import { useChapterProblemContext } from '../../../ChapterProblemContext';
import { ChapterProblemSubmissionsTable } from '../ChapterProblemSubmissionsTable/ChapterProblemSubmissionsTable';

import * as toastActions from '../../../../../../../../../../../modules/toast/toastActions';

const PAGE_SIZE = 20;

export default function ChapterProblemSubmissionsPage() {
  const { worksheet } = useChapterProblemContext();
  const problemAlias = worksheet?.problem?.alias;
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useSession();
  const userJid = user?.jid;
  const username = user?.username;
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(course.jid, chapterAlias));

  const page = +(location.search.page || 1);
  const isShowAll = (location.pathname + '/').includes('/all/');
  const usernameFilter = isShowAll ? undefined : username;

  const { data: response } = useQuery(
    chapterProgrammingSubmissionsQueryOptions(chapter.jid, { problemAlias, username: usernameFilter, page })
  );

  const regradeSubmissionMutation = useMutation(regradeChapterProgrammingSubmissionMutationOptions(chapter.jid));
  const regradeSubmissionsMutation = useMutation(regradeChapterProgrammingSubmissionsMutationOptions(chapter.jid));

  const onChangeFilterShowAll = ({ target }) => {
    if (target.checked) {
      navigate({ to: (location.pathname + '/all').replace('//', '/') });
    } else {
      const idx = location.pathname.lastIndexOf('/all');
      navigate({ to: location.pathname.substring(0, idx) });
    }
  };

  const onRegradeSubmission = async submissionJid => {
    await regradeSubmissionMutation.mutateAsync(submissionJid, {
      onSuccess: () => {
        toastActions.showSuccessToast('Regrade in progress.');
      },
    });
  };

  const onRegradeSubmissions = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await regradeSubmissionsMutation.mutateAsync(
        { problemAlias },
        {
          onSuccess: () => {
            toastActions.showSuccessToast('Regrade in progress.');
          },
        }
      );
    }
  };

  const renderHeader = () => {
    return (
      <div className="content-card__header">
        <div className="action-buttons float-left">{renderRegradeAllButton()}</div>
        <div className="clearfix" />
      </div>
    );
  };

  const renderFilter = () => {
    return userJid && <Switch label="Show all submissions" checked={isShowAll} onChange={onChangeFilterShowAll} />;
  };

  const renderRegradeAllButton = () => {
    if (!response || !response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={onRegradeSubmissions} />;
  };

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ChapterProblemSubmissionsTable
        course={course}
        chapterAlias={chapterAlias}
        problemAlias={problemAlias}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        onRegrade={onRegradeSubmission}
      />
    );
  };

  return (
    <ContentCard>
      {renderFilter()}
      {renderHeader()}
      {renderSubmissions()}
      {response && <PaginationV2 pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
