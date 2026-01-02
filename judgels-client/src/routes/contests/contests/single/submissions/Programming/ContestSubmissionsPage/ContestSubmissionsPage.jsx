import { useLocation, useNavigate } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { withBreadcrumb } from '../../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { RegradeAllButton } from '../../../../../../../components/RegradeAllButton/RegradeAllButton';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { reallyConfirm } from '../../../../../../../utils/confirmation';
import { selectContest } from '../../../../modules/contestSelectors';
import { ContestSubmissionsTable } from '../ContestSubmissionsTable/ContestSubmissionsTable';

import * as contestSubmissionActions from '../modules/contestSubmissionActions';

const PAGE_SIZE = 20;

function ContestSubmissionsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const contest = useSelector(selectContest);

  const username = location.search.username;
  const problemAlias = location.search.problemAlias;

  const [state, setState] = useState({
    response: undefined,
    isFilterLoading: false,
    isRegradingAll: false,
  });

  useEffect(() => {
    if (username || problemAlias) {
      setState(prevState => ({ ...prevState, isFilterLoading: true }));
    }
  }, [username, problemAlias]);

  const render = () => {
    return (
      <ContentCard>
        <h3>Submissions</h3>
        <hr />
        {renderHeader()}
        {renderSubmissions()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderHeader = () => {
    return (
      <div className="content-card__header">
        <div className="float-left">{renderRegradeAllButton()}</div>
        <div className="float-right">{renderFilterWidget()}</div>
        <div className="clearfix" />
      </div>
    );
  };

  const renderRegradeAllButton = () => {
    if (!state.response || !state.response.config.canManage) {
      return null;
    }
    return <RegradeAllButton onRegradeAll={onRegradeAll} isRegradingAll={state.isRegradingAll} />;
  };

  const renderFilterWidget = () => {
    const { response, isFilterLoading } = state;
    if (!response) {
      return null;
    }
    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids, canSupervise } = config;
    if (!canSupervise) {
      return null;
    }

    return (
      <SubmissionFilterWidget
        usernames={userJids.map(jid => profilesMap[jid] && profilesMap[jid].username)}
        problemAliases={problemJids.map(jid => problemAliasesMap[jid])}
        username={username}
        problemAlias={problemAlias}
        onFilter={onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  const renderSubmissions = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissions, config, profilesMap, problemAliasesMap } = response;
    if (submissions.page.length === 0) {
      return (
        <p>
          <small>No submissions.</small>
        </p>
      );
    }

    return (
      <ContestSubmissionsTable
        contest={contest}
        submissions={submissions.page}
        canSupervise={config.canSupervise}
        canManage={config.canManage}
        profilesMap={profilesMap}
        problemAliasesMap={problemAliasesMap}
        onRegrade={onRegrade}
      />
    );
  };

  const renderPagination = () => {
    const key = '' + username + problemAlias;
    return <Pagination key={key} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSubmissions(nextPage);
    return data.totalCount;
  };

  const refreshSubmissions = async page => {
    const response = await dispatch(contestSubmissionActions.getSubmissions(contest.jid, username, problemAlias, page));
    setState(prevState => ({ ...prevState, response, isFilterLoading: false }));
    return response.data;
  };

  const onRegrade = async submissionJid => {
    await onRegrade(submissionJid);
    await refreshSubmissions(location.search.page);
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      setState(prevState => ({ ...prevState, isRegradingAll: true }));
      await dispatch(contestSubmissionActions.regradeSubmissions(contest.jid, username, problemAlias));
      setState(prevState => ({ ...prevState, isRegradingAll: false }));
      await refreshSubmissions(location.search.page);
    }
  };

  const onFilter = async filter => {
    navigate({ search: filter });
  };

  return render();
}

export default withBreadcrumb('Submissions')(ContestSubmissionsPage);
