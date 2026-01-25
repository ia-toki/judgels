import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestSupervisorAddDialog } from '../ContestSupervisorAddDialog/ContestSupervisorAddDialog';
import { ContestSupervisorRemoveDialog } from '../ContestSupervisorRemoveDialog/ContestSupervisorRemoveDialog';
import { ContestSupervisorsTable } from '../ContestSupervisorsTable/ContestSupervisorsTable';

import * as contestSupervisorActions from '../../modules/contestSupervisorActions';

import './ContestSupervisorsPage.scss';

const PAGE_SIZE = 250;

export default function ContestSupervisorsPage() {
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);

  const [state, setState] = useState({
    response: undefined,
    lastRefreshSupervisorsTime: 0,
  });

  const render = () => {
    return (
      <ContentCard>
        <h3>Supervisors</h3>
        <hr />
        {renderAddRemoveDialogs()}
        {renderSupervisors()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderSupervisors = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: supervisors, profilesMap } = response;
    if (supervisors.page.length === 0) {
      return (
        <p>
          <small>No supervisors.</small>
        </p>
      );
    }

    return <ContestSupervisorsTable supervisors={supervisors.page} profilesMap={profilesMap} />;
  };

  const renderPagination = () => {
    const { lastRefreshSupervisorsTime } = state;

    return <Pagination key={lastRefreshSupervisorsTime} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshSupervisors(nextPage);
    return data.totalCount;
  };

  const refreshSupervisors = async page => {
    const response = await dispatch(contestSupervisorActions.getSupervisors(contest.jid, page));
    setState(prevState => ({ ...prevState, response }));
    return response.data;
  };

  const renderAddRemoveDialogs = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    return (
      <div className="content-card__header">
        <ContestSupervisorAddDialog contest={contest} onUpsertSupervisors={upsertSupervisors} />
        <ContestSupervisorRemoveDialog contest={contest} onDeleteSupervisors={deleteSupervisors} />
        <div className="clearfix" />
      </div>
    );
  };

  const upsertSupervisors = async (contestJid, data) => {
    const response = await dispatch(contestSupervisorActions.upsertSupervisors(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshSupervisorsTime: new Date().getTime() }));
    return response;
  };

  const deleteSupervisors = async (contestJid, data) => {
    const response = await dispatch(contestSupervisorActions.deleteSupervisors(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshSupervisorsTime: new Date().getTime() }));
    return response;
  };

  return render();
}
