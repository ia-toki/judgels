import { Button, ButtonGroup, HTMLTable, Intent } from '@blueprintjs/core';
import { Refresh, Search } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { Link, useLocation, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { FormattedRelative } from '../../../../../../../../components/FormattedRelative/FormattedRelative';
import ItemSubmissionUserFilter from '../../../../../../../../components/ItemSubmissionUserFilter/ItemSubmissionUserFilter';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../../../components/Pagination/Pagination';
import { FormattedAnswer } from '../../../../../../../../components/SubmissionDetails/Bundle/FormattedAnswer/FormattedAnswer';
import { VerdictTag } from '../../../../../../../../components/SubmissionDetails/Bundle/VerdictTag/VerdictTag';
import { UserRef } from '../../../../../../../../components/UserRef/UserRef';
import { callAction } from '../../../../../../../../modules/callAction';
import {
  problemSetBySlugQueryOptions,
  problemSetProblemQueryOptions,
} from '../../../../../../../../modules/queries/problemSet';
import { useSession } from '../../../../../../../../modules/session';
import { reallyConfirm } from '../../../../../../../../utils/confirmation';

import * as problemSetSubmissionActions from '../modules/problemSetSubmissionActions';

import '../../../../../../../../components/SubmissionsTable/Bundle/ItemSubmissionsTable.scss';

const PAGE_SIZE = 20;

export default function ProblemSubmissionsPage() {
  const { problemSetSlug, problemAlias } = useParams({ strict: false });
  const location = useLocation();
  const { user } = useSession();
  const userJid = user?.jid;
  const { data: problemSet } = useSuspenseQuery(problemSetBySlugQueryOptions(problemSetSlug));
  const { data: problem } = useSuspenseQuery(problemSetProblemQueryOptions(problemSet.jid, problemAlias));

  const [state, setState] = useState({
    response: undefined,
  });

  useEffect(() => {
    refreshSubmissions();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3>Results</h3>
        <hr />
        <ItemSubmissionUserFilter />
        {renderRegradeAllButton()}
        {renderSubmissions()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderSubmissions = () => {
    const response = state.response;
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
                  {(canManage || userJid === item.userJid) && (
                    <Link
                      to={`/problems/${problemSet.slug}/${problem.alias}/results/users/${
                        profilesMap[item.userJid].username
                      }`}
                    >
                      <Button icon={<Search />} intent={Intent.NONE} small />
                    </Link>
                  )}
                  {canManage && (
                    <Button icon={<Refresh />} intent={Intent.NONE} small onClick={() => onRegrade(item.jid)} />
                  )}
                </ButtonGroup>
              </td>
            </tr>
          ))}
        </tbody>
      </HTMLTable>
    );
  };

  const renderPagination = () => {
    return <Pagination pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const refreshSubmissions = async page => {
    const response = await callAction(
      problemSetSubmissionActions.getSubmissions(problemSet.jid, undefined, problem.alias, page)
    );
    setState({ response });
    return response.data;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSubmissions(nextPage);
    return data.totalCount;
  };

  const onRegrade = async submissionJid => {
    await callAction(problemSetSubmissionActions.regradeSubmission(submissionJid));
    await refreshSubmissions(location.search.page);
  };

  const onRegradeAll = async () => {
    if (reallyConfirm('Regrade all submissions in all pages?')) {
      await callAction(problemSetSubmissionActions.regradeSubmissions(problemSet.jid, undefined, problem.problemJid));
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

  return render();
}
