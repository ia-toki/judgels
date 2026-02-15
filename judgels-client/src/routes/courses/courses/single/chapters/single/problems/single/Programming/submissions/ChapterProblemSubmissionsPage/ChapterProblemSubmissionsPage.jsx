import { Switch } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import { callAction } from '../../../../../../../../../../../modules/callAction';
import {
  courseBySlugQueryOptions,
  courseChapterQueryOptions,
} from '../../../../../../../../../../../modules/queries/course';
import { useSession } from '../../../../../../../../../../../modules/session';
import { reallyConfirm } from '../../../../../../../../../../../utils/confirmation';
import { useChapterProblemContext } from '../../../ChapterProblemContext';
import { ChapterProblemSubmissionsTable } from '../ChapterProblemSubmissionsTable/ChapterProblemSubmissionsTable';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

const PAGE_SIZE = 20;

export default function ChapterProblemSubmissionsPage() {
  const { worksheet } = useChapterProblemContext();
  const problemAlias = worksheet?.problem?.alias;
  const { courseSlug, chapterAlias } = useParams({ strict: false });
  const location = useLocation();
  const navigate = useNavigate();
  const { token, user } = useSession();
  const userJid = user?.jid;
  const username = user?.username;
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const { data: chapter } = useSuspenseQuery(courseChapterQueryOptions(token, course.jid, chapterAlias));

  const [state, setState] = useState({
    response: undefined,
  });

  const render = () => {
    return (
      <ContentCard>
        {renderFilter()}
        {renderHeader()}
        {renderSubmissions()}
        {renderPagination()}
      </ContentCard>
    );
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
    return (
      userJid && (
        <Switch label="Show all submissions" checked={isFilterShowAllChecked()} onChange={onChangeFilterShowAll} />
      )
    );
  };

  const isFilterShowAllChecked = () => {
    return (location.pathname + '/').includes('/all/');
  };

  const onChangeFilterShowAll = ({ target }) => {
    if (target.checked) {
      navigate({ to: (location.pathname + '/all').replace('//', '/') });
    } else {
      const idx = location.pathname.lastIndexOf('/all');
      navigate({ to: location.pathname.substring(0, idx) });
    }
  };

  const renderRegradeAllButton = () => {
    if (!state.response || !state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={onRegradeSubmissions} />;
  };

  const renderSubmissions = () => {
    const { response } = state;
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

  const renderPagination = () => {
    const key = '' + isFilterShowAllChecked();
    return <Pagination key={key} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSubmissions(nextPage);
    return data.totalCount;
  };

  const refreshSubmissions = async page => {
    const usernameFilter = isFilterShowAllChecked() ? undefined : username;
    const response = await callAction(
      chapterProblemSubmissionActions.getSubmissions(chapter.jid, problemAlias, usernameFilter, page)
    );
    setState({ response });
    return response.data;
  };

  const onRegradeSubmission = async submissionJid => {
    await callAction(chapterProblemSubmissionActions.regradeSubmission(submissionJid));
    await refreshSubmissions(location.search.page);
  };

  const onRegradeSubmissions = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await callAction(chapterProblemSubmissionActions.regradeSubmissions(chapter.jid, undefined, problemAlias));
      await refreshSubmissions(location.search.page);
    }
  };

  return render();
}
