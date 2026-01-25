import { Button, Intent } from '@blueprintjs/core';
import { Refresh } from '@blueprintjs/icons';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { reallyConfirm } from '../../../../../../utils/confirmation';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestContestantAddDialog } from '../ContestContestantAddDialog/ContestContestantAddDialog';
import { ContestContestantRemoveDialog } from '../ContestContestantRemoveDialog/ContestContestantRemoveDialog';
import { ContestContestantsTable } from '../ContestContestantsTable/ContestContestantsTable';

import * as contestActions from '../../../modules/contestActions';
import * as contestContestantActions from '../../modules/contestContestantActions';

import './ContestContestantsPage.scss';

const PAGE_SIZE = 1000;

export default function ContestContestantsPage() {
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);

  const [state, setState] = useState({
    response: undefined,
    lastRefreshContestantsTime: 0,
  });

  const render = () => {
    return (
      <ContentCard>
        <h3>Contestants</h3>
        <hr />
        {renderAddRemoveDialogs()}
        {renderContestants()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderContestants = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: contestants, virtualModuleConfig, profilesMap } = response;
    if (contestants.page.length === 0) {
      return (
        <p>
          <small>No contestants.</small>
        </p>
      );
    }

    return (
      <ContestContestantsTable
        contest={contest}
        virtualModuleConfig={virtualModuleConfig}
        contestants={contestants.page}
        profilesMap={profilesMap}
        now={new Date().getTime()}
      />
    );
  };

  const renderPagination = () => {
    const { lastRefreshContestantsTime } = state;

    return <Pagination key={lastRefreshContestantsTime} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshContestants(nextPage);
    return data.totalCount;
  };

  const refreshContestants = async page => {
    const response = await dispatch(contestContestantActions.getContestants(contest.jid, page));
    setState(prevState => ({ ...prevState, response }));
    return response.data;
  };

  const renderAddRemoveDialogs = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }
    const { data: contestants } = response;
    const isVirtualContest = contestants.page.some(contestant => contestant.contestStartTime !== null);
    return (
      <div className="content-card__header">
        <ContestContestantAddDialog contest={contest} onUpsertContestants={upsertContestants} />
        <ContestContestantRemoveDialog contest={contest} onDeleteContestants={deleteContestants} />
        {isVirtualContest && (
          <Button
            className="contest-contestant-dialog-button"
            intent={Intent.DANGER}
            icon={<Refresh />}
            onClick={onResetVirtualContest}
          >
            Reset all contestant virtual start times
          </Button>
        )}
        <div className="clearfix" />
      </div>
    );
  };

  const onResetVirtualContest = async () => {
    if (reallyConfirm('Are you sure to reset all contestant virtual start times?')) {
      await dispatch(contestActions.resetVirtualContest(contest.jid));
      setState(prevState => ({ ...prevState, lastRefreshContestantsTime: new Date().getTime() }));
    }
  };

  const upsertContestants = async (contestJid, data) => {
    const response = await dispatch(contestContestantActions.upsertContestants(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshContestantsTime: new Date().getTime() }));
    return response;
  };

  const deleteContestants = async (contestJid, data) => {
    const response = await dispatch(contestContestantActions.deleteContestants(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshContestantsTime: new Date().getTime() }));
    return response;
  };

  return render();
}
