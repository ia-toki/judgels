import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestManagersQueryOptions } from '../../../../../../modules/queries/contestManager';
import { ContestManagerAddDialog } from '../ContestManagerAddDialog/ContestManagerAddDialog';
import { ContestManagerRemoveDialog } from '../ContestManagerRemoveDialog/ContestManagerRemoveDialog';
import { ContestManagersTable } from '../ContestManagersTable/ContestManagersTable';

import './ContestManagersPage.scss';

const PAGE_SIZE = 250;

export default function ContestManagersPage() {
  const location = useLocation();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const page = +(location.search.page || 1);

  const { data: response } = useQuery(contestManagersQueryOptions(contest.jid, { page }));

  const renderManagers = () => {
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

  const renderAddRemoveDialogs = () => {
    if (!response) {
      return null;
    }
    if (!response.config.canManage) {
      return null;
    }
    return (
      <div className="content-card__header">
        <ContestManagerAddDialog contest={contest} />
        <ContestManagerRemoveDialog contest={contest} />
        <div className="clearfix" />
      </div>
    );
  };

  return (
    <ContentCard>
      <h3>Managers</h3>
      <hr />
      {renderAddRemoveDialogs()}
      {renderManagers()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
