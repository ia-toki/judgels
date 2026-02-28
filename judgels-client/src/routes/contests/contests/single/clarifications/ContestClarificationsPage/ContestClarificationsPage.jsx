import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ClarificationFilterWidget } from '../../../../../../components/ClarificationFilterWidget/ClarificationFilterWidget';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { askDesktopNotificationPermission } from '../../../../../../modules/notification/notification';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestClarificationsQueryOptions } from '../../../../../../modules/queries/contestClarification';
import { useWebPrefs } from '../../../../../../modules/webPrefs';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import { ContestClarificationCreateDialog } from '../ContestClarificationCreateDialog/ContestClarificationCreateDialog';

const PAGE_SIZE = 20;

function ContestClarificationsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { statementLanguage } = useWebPrefs();

  const status = location.search.status;
  const page = +(location.search.page || 1);

  const { data: response, isLoading } = useQuery(
    contestClarificationsQueryOptions(contest.jid, { page, status, language: statementLanguage })
  );

  const [openAnswerBoxJid, setOpenAnswerBoxJid] = useState(undefined);

  useEffect(() => {
    askDesktopNotificationPermission();
  }, []);

  const toggleAnswerBox = clarification => {
    setOpenAnswerBoxJid(clarification?.jid);
  };

  const renderCreateDialog = () => {
    if (!response) {
      return null;
    }
    if (!response.config.canCreate) {
      return null;
    }

    return (
      <ContestClarificationCreateDialog
        contest={contest}
        problemJids={response.config.problemJids}
        problemAliasesMap={response.problemAliasesMap}
        problemNamesMap={response.problemNamesMap}
      />
    );
  };

  const renderFilterWidget = () => {
    if (!response) {
      return null;
    }
    if (!response.config.canSupervise) {
      return null;
    }

    return (
      <ClarificationFilterWidget
        statuses={['ASKED']}
        status={status}
        onFilter={onFilter}
        isLoading={isLoading && !!status}
      />
    );
  };

  const onFilter = async newFilter => {
    navigate({ search: newFilter });
  };

  const renderClarifications = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: clarifications, config, profilesMap, problemAliasesMap, problemNamesMap } = response;
    if (clarifications.page.length === 0) {
      return (
        <p>
          <small>No clarifications.</small>
        </p>
      );
    }

    const { canSupervise, canManage } = config;

    return clarifications.page.map(clarification => (
      <div className="content-card__section" key={clarification.jid}>
        <ContestClarificationCard
          contest={contest}
          clarification={clarification}
          canSupervise={canSupervise}
          canManage={canManage}
          askerProfile={canSupervise ? profilesMap[clarification.userJid] : undefined}
          answererProfile={
            canSupervise && clarification.answererJid ? profilesMap[clarification.answererJid] : undefined
          }
          problemAlias={problemAliasesMap[clarification.topicJid]}
          problemName={problemNamesMap[clarification.topicJid]}
          isAnswerBoxOpen={openAnswerBoxJid === clarification.jid}
          onToggleAnswerBox={toggleAnswerBox}
        />
      </div>
    ));
  };

  return (
    <ContentCard>
      <h3>Clarifications</h3>
      <hr />
      {renderCreateDialog()}
      {renderFilterWidget()}
      {renderClarifications()}
      {response && <Pagination pageSize={PAGE_SIZE} totalCount={response.data.totalCount} />}
    </ContentCard>
  );
}

export default ContestClarificationsPage;
