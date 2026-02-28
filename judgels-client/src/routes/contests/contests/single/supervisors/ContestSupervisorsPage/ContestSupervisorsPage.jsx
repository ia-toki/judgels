import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestSupervisorsQueryOptions } from '../../../../../../modules/queries/contestSupervisor';
import { ContestSupervisorAddDialog } from '../ContestSupervisorAddDialog/ContestSupervisorAddDialog';
import { ContestSupervisorRemoveDialog } from '../ContestSupervisorRemoveDialog/ContestSupervisorRemoveDialog';
import { ContestSupervisorsTable } from '../ContestSupervisorsTable/ContestSupervisorsTable';

import './ContestSupervisorsPage.scss';

const PAGE_SIZE = 250;

export default function ContestSupervisorsPage() {
  const location = useLocation();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const page = location.search.page;

  const { data: response } = useQuery(contestSupervisorsQueryOptions(contest.jid, { page }));

  const renderSupervisors = () => {
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

  const renderAddRemoveDialogs = () => {
    if (!response) {
      return null;
    }
    return (
      <div className="content-card__header">
        <ContestSupervisorAddDialog contest={contest} />
        <ContestSupervisorRemoveDialog contest={contest} />
        <div className="clearfix" />
      </div>
    );
  };

  return (
    <ContentCard>
      <h3>Supervisors</h3>
      <hr />
      {renderAddRemoveDialogs()}
      {renderSupervisors()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}
