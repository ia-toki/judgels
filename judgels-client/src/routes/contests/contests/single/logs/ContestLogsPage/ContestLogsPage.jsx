import { parse, stringify } from 'query-string';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation, useNavigate } from 'react-router';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { SubmissionFilterWidget } from '../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestLogsTable } from '../ContestLogsTable/ContestLogsTable';

import * as contestLogActions from '../modules/contestLogActions';

const PAGE_SIZE = 100;

function ContestLogsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const contest = useSelector(selectContest);

  const queries = parse(location.search);
  const username = queries.username;
  const problemAlias = queries.problemAlias;

  const [state, setState] = useState({
    response: undefined,
    isFilterLoading: false,
  });

  useEffect(() => {
    if (username || problemAlias) {
      setState(prevState => ({ ...prevState, isFilterLoading: true }));
    }
  }, [username, problemAlias]);

  const render = () => {
    return (
      <ContentCard>
        <h3>Logs</h3>
        <hr />
        {renderFilterWidget()}
        {renderLogs()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderFilterWidget = () => {
    const { response, isFilterLoading } = state;
    if (!response) {
      return null;
    }
    const { config, profilesMap, problemAliasesMap } = response;
    const { userJids, problemJids } = config;
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

  const renderLogs = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: logs, profilesMap, problemAliasesMap } = response;
    if (logs.page.length === 0) {
      return (
        <p>
          <small>No logs.</small>
        </p>
      );
    }

    return <ContestLogsTable logs={logs.page} profilesMap={profilesMap} problemAliasesMap={problemAliasesMap} />;
  };

  const renderPagination = () => {
    const key = '' + username + problemAlias;
    return <Pagination key={key} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshLogs(nextPage);
    return data.totalCount;
  };

  const refreshLogs = async page => {
    const response = await dispatch(contestLogActions.getLogs(contest.jid, username, problemAlias, page));
    setState({ response, isFilterLoading: false });
    return response.data;
  };

  const onFilter = async newFilter => {
    navigate({ search: stringify(newFilter) });
  };

  return render();
}

export default withBreadcrumb('Logs')(ContestLogsPage);
