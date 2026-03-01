import { Button, ButtonGroup, HTMLTable, Intent } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useLocation, useNavigate, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { FormattedRelative } from '../../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { FormattedAnswer } from '../../../../../../../components/SubmissionDetails/Bundle/FormattedAnswer/FormattedAnswer';
import { VerdictTag } from '../../../../../../../components/SubmissionDetails/Bundle/VerdictTag/VerdictTag';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import {
  contestBundleSubmissionsQueryOptions,
  regradeBundleSubmissionsMutationOptions,
} from '../../../../../../../modules/queries/contestSubmissionBundle';
import { reallyConfirm } from '../../../../../../../utils/confirmation';

import * as toastActions from '../../../../../../../modules/toast/toastActions';

import '../../../../../../../components/SubmissionsTable/Bundle/ItemSubmissionsTable.scss';

const PAGE_SIZE = 20;

function ContestSubmissionsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const username = location.search.username;
  const problemAlias = location.search.problemAlias;
  const page = location.search.page;

  const { data: response, isLoading } = useQuery(
    contestBundleSubmissionsQueryOptions(contest.jid, { username, problemAlias, page })
  );

  const regradeSubmissionsMutation = useMutation(regradeBundleSubmissionsMutationOptions(contest.jid));

  const renderSubmissions = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data, profilesMap, problemAliasesMap, itemNumbersMap, itemTypesMap } = response;
    const canManage = response.config.canManage;

    return (
      <HTMLTable striped className="table-list-condensed item-submissions-table">
        <thead>
          <tr>
            <th>User</th>
            <th className="col-prob">Prob</th>
            <th className="col-item-num">No</th>
            <th>Answer</th>
            {canManage && <th className="col-verdict">Verdict</th>}
            <th>Time</th>
            <th className="col-action" />
          </tr>
        </thead>
        <tbody>
          {data.page.map(item => (
            <tr key={item.jid}>
              <td>
                <UserRef profile={profilesMap[item.userJid]} />
              </td>
              <td className="col-prob">{problemAliasesMap[item.problemJid] || '-'}</td>
              <td className="col-item-num">{itemNumbersMap[item.itemJid] || '-'}</td>
              <td>
                <FormattedAnswer answer={item.answer} type={itemTypesMap[item.itemJid]} />
              </td>
              {canManage && (
                <td className="col-verdict">{item.grading ? <VerdictTag verdict={item.grading.verdict} /> : '-'}</td>
              )}
              <td>
                <FormattedRelative value={item.time} />
              </td>
              <td className="col-action">
                <ButtonGroup minimal className="action-button-group">
                  <Link to={`/contests/${contest.slug}/submissions/users/${profilesMap[item.userJid].username}`}>
                    <Button icon={<Search />} intent={Intent.NONE} small />
                  </Link>
                </ButtonGroup>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      await regradeSubmissionsMutation.mutateAsync(
        { username, problemAlias },
        {
          onSuccess: () => toastActions.showSuccessToast('Regraded.'),
        }
      );
    }
  };

  const renderRegradeAllButton = () => {
    if (!response || !response.config.canManage) {
      return null;
    }

    return (
      <Button
        className="item-submissions-table__regrade-button"
        intent="primary"
        icon={<Refresh />}
        onClick={onRegradeAll}
      >
        Regrade all pages
      </Button>
    );
  };

  const renderFilterWidget = () => {
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
        isLoading={isLoading && !!(username || problemAlias)}
      />
    );
  };

  const onFilter = async filter => {
    navigate({ search: filter });
  };

  return (
    <ContentCard>
      <h3>Submissions</h3>
      <hr />
      {renderRegradeAllButton()}
      {renderFilterWidget()}
      {renderSubmissions()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}

export default ContestSubmissionsPage;
