import { Button, ButtonGroup, HTMLTable, Intent } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../components/ContentCard/ContentCard';
import { FormattedRelative } from '../../../../../../../components/FormattedRelative/FormattedRelative';
import { LoadingState } from '../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../components/Pagination/Pagination';
import { FormattedAnswer } from '../../../../../../../components/SubmissionDetails/Bundle/FormattedAnswer/FormattedAnswer';
import { VerdictTag } from '../../../../../../../components/SubmissionDetails/Bundle/VerdictTag/VerdictTag';
import { SubmissionFilterWidget } from '../../../../../../../components/SubmissionFilterWidget/SubmissionFilterWidget';
import { UserRef } from '../../../../../../../components/UserRef/UserRef';
import { callAction } from '../../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../../modules/queries/contest';
import { reallyConfirm } from '../../../../../../../utils/confirmation';

import * as contestSubmissionActions from '../modules/contestSubmissionActions';

import '../../../../../../../components/SubmissionsTable/Bundle/ItemSubmissionsTable.scss';

const PAGE_SIZE = 20;

function ContestSubmissionsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const username = location.search.username;
  const problemAlias = location.search.problemAlias;

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
        <h3>Submissions</h3>
        <hr />
        {renderRegradeAllButton()}
        {renderFilterWidget()}
        {renderSubmissions()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderSubmissions = () => {
    const { response } = state;
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

  const renderPagination = () => {
    const key = '' + username + problemAlias;
    return <Pagination key={key} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const refreshSubmissions = async page => {
    const response = await callAction(
      contestSubmissionActions.getSubmissions(contest.jid, username, problemAlias, page)
    );
    setState({ response, isFilterLoading: false });
    return response;
  };

  const onChangePage = async nextPage => {
    const response = await refreshSubmissions(nextPage);
    return response.data.totalCount;
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages for the current filter?')) {
      await callAction(contestSubmissionActions.regradeSubmissions(contest.jid, username, problemAlias));
      await refreshSubmissions(location.search.page);
    }
  };

  const renderRegradeAllButton = () => {
    if (!state.response || !state.response.config.canManage) {
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

  const onFilter = async filter => {
    navigate({ search: filter });
  };

  return render();
}

export default ContestSubmissionsPage;
