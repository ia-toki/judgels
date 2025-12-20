import { parse } from 'query-string';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation } from 'react-router-dom';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../../components/RegradeAllButton/RegradeAllButton';
import SubmissionUserFilter from '../../../../../../../../components/SubmissionUserFilter/SubmissionUserFilter';
import { selectMaybeUserJid, selectMaybeUsername } from '../../../../../../../../modules/session/sessionSelectors';
import { reallyConfirm } from '../../../../../../../../utils/confirmation';
import { selectProblemSet } from '../../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../../modules/problemSetProblemSelectors';
import { ProblemSubmissionsTable } from '../ProblemSubmissionsTable/ProblemSubmissionsTable';

import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

const PAGE_SIZE = 20;

export default function ProblemSubmissionsPage() {
  const location = useLocation();
  const dispatch = useDispatch();
  const userJid = useSelector(selectMaybeUserJid);
  const username = useSelector(selectMaybeUsername);
  const problemSet = useSelector(selectProblemSet);
  const problem = useSelector(selectProblemSetProblem);

  const [state, setState] = useState({
    response: undefined,
  });

  const render = () => {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {renderUserFilter()}
        {renderHeader()}
        {renderSubmissions()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderUserFilter = () => {
    return userJid && <SubmissionUserFilter />;
  };

  const isUserFilterMine = () => {
    return (location.pathname + '/').includes('/mine/');
  };

  const renderHeader = () => {
    return <div className="content-card__header">{renderRegradeAllButton()}</div>;
  };

  const renderRegradeAllButton = () => {
    if (!state.response || !state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={onRegradeAll} />;
  };

  const renderSubmissions = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.totalCount === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ProblemSubmissionsTable
        problemSet={problemSet}
        problem={problem}
        submissions={submissions.page}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={onRegrade}
      />
    );
  };

  const renderPagination = () => {
    return <Pagination key={'' + isUserFilterMine()} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSubmissions(nextPage);
    return data.totalCount;
  };

  const refreshSubmissions = async page => {
    const usernameFilter = isUserFilterMine() ? username : undefined;
    const response = await dispatch(
      problemSetSubmissionActions.getSubmissions(undefined, usernameFilter, problem.problemJid, page)
    );
    setState({ response });
    return response.data;
  };

  const onRegrade = async submissionJid => {
    await dispatch(problemSetSubmissionActions.regradeSubmission(submissionJid));
    const queries = parse(location.search);
    await refreshSubmissions(queries.page);
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await dispatch(problemSetSubmissionActions.regradeSubmissions(undefined, undefined, problem.problemJid));
      const queries = parse(location.search);
      await refreshSubmissions(queries.page);
    }
  };

  return render();
}
