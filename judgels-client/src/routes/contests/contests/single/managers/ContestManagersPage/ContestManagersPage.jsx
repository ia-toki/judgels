import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { ContestManagerAddDialog } from '../ContestManagerAddDialog/ContestManagerAddDialog';
import { ContestManagerRemoveDialog } from '../ContestManagerRemoveDialog/ContestManagerRemoveDialog';
import { ContestManagersTable } from '../ContestManagersTable/ContestManagersTable';

import * as contestManagerActions from '../modules/contestManagerActions';

import './ContestManagersPage.scss';

const PAGE_SIZE = 250;

export default function ContestManagersPage() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
    lastRefreshManagersTime: 0,
  });

  const render = () => {
    return (
      <ContentCard>
        <h3>Managers</h3>
        <hr />
        {renderAddRemoveDialogs()}
        {renderManagers()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const renderManagers = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: managers, profilesMap } = response;
    if (managers.page.length === 0) {
      return (
        <p>
          <small>No managers.</small>
        </p>
      );
    }

    return <ContestManagersTable managers={managers.page} profilesMap={profilesMap} />;
  };

  const renderPagination = () => {
    const { lastRefreshManagersTime } = state;
    const key = lastRefreshManagersTime || 0;

    return <Pagination key={key} currentPage={1} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const onChangePage = async nextPage => {
    const data = await refreshManagers(nextPage);
    return data.totalCount;
  };

  const refreshManagers = async page => {
    const response = await dispatch(contestManagerActions.getManagers(contest.jid, page));
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
    return (
      <div className="content-card__header">
        <ContestManagerAddDialog contest={contest} onUpsertManagers={upsertManagers} />
        <ContestManagerRemoveDialog contest={contest} onDeleteManagers={deleteManagers} />
        <div className="clearfix" />
      </div>
    );
  };

  const upsertManagers = async (contestJid, data) => {
    const response = await dispatch(contestManagerActions.upsertManagers(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshManagersTime: new Date().getTime() }));
    return response;
  };

  const deleteManagers = async (contestJid, data) => {
    const response = await dispatch(contestManagerActions.deleteManagers(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshManagersTime: new Date().getTime() }));
    return response;
  };

  return render();
}
