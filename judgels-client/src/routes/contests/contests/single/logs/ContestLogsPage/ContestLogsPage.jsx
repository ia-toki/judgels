import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { SubmissionFilterWidget } from '../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestLogsQueryOptions } from '../../../../../../modules/queries/contestLog';
import { ContestLogsTable } from '../ContestLogsTable/ContestLogsTable';

const PAGE_SIZE = 100;

function ContestLogsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });

  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const username = location.search.username;
  const problemAlias = location.search.problemAlias;
  const page = location.search.page;

  const { data: response, isLoading } = useQuery(
    contestLogsQueryOptions(contest.jid, { username, problemAlias, page })
  );

  const renderFilterWidget = () => {
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
        isLoading={isLoading && !!(username || problemAlias)}
      />
    );
  };

  const renderLogs = () => {
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

  const onFilter = async newFilter => {
    navigate({ search: newFilter });
  };

  return (
    <ContentCard>
      <h3>Logs</h3>
      <hr />
      {renderFilterWidget()}
      {renderLogs()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}

export default ContestLogsPage;
