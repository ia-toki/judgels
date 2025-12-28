import { Switch } from '@blueprintjs/core';
import { parse } from 'query-string';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation, useNavigate, useParams } from 'react-router';

import { ContentCard } from '../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import {
  selectMaybeUserJid,
  selectMaybeUsername,
} from '../../../../../../../../../../../modules/session/sessionSelectors';
import { reallyConfirm } from '../../../../../../../../../../../utils/confirmation';
import { selectCourse } from '../../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../../modules/courseChapterSelectors';
import { ChapterProblemSubmissionsTable } from '../ChapterProblemSubmissionsTable/ChapterProblemSubmissionsTable';

import * as chapterProblemSubmissionActions from '../modules/chapterProblemSubmissionActions';

const PAGE_SIZE = 20;

export default function ChapterProblemSubmissionsPage() {
  const { problemAlias } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const userJid = useSelector(selectMaybeUserJid);
  const username = useSelector(selectMaybeUsername);
  const course = useSelector(selectCourse);
  const chapter = useSelector(selectCourseChapter);

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
      navigate((location.pathname + '/all').replace('//', '/'));
    } else {
      const idx = location.pathname.lastIndexOf('/all');
      navigate(location.pathname.substr(0, idx));
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
        chapter={chapter}
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
    const response = await dispatch(
      chapterProblemSubmissionActions.getSubmissions(chapter.jid, problemAlias, usernameFilter, page)
    );
    setState({ response });
    return response.data;
  };

  const onRegradeSubmission = async submissionJid => {
    await dispatch(chapterProblemSubmissionActions.regradeSubmission(submissionJid));
    const queries = parse(location.search);
    await refreshSubmissions(queries.page);
  };

  const onRegradeSubmissions = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await dispatch(chapterProblemSubmissionActions.regradeSubmissions(chapter.jid, undefined, problemAlias));
      const queries = parse(location.search);
      await refreshSubmissions(queries.page);
    }
  };

  return render();
}
