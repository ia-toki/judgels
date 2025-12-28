import { parse, stringify } from 'query-string';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation, useNavigate } from 'react-router';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { ClarificationFilterWidget } from '../../../../../../components/ClarificationFilterWidget/ClarificationFilterWidget';
import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../../components/Pagination/Pagination';
import { askDesktopNotificationPermission } from '../../../../../../modules/notification/notification';
import { selectStatementLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../modules/contestSelectors';
import { ContestClarificationCard } from '../ContestClarificationCard/ContestClarificationCard';
import { ContestClarificationCreateDialog } from '../ContestClarificationCreateDialog/ContestClarificationCreateDialog';

import * as contestClarificationActions from '../modules/contestClarificationActions';

const PAGE_SIZE = 20;

function ContestClarificationsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const contest = useSelector(selectContest);
  const statementLanguage = useSelector(selectStatementLanguage);

  const queries = parse(location.search);
  const status = queries.status;

  const [state, setState] = useState({
    response: undefined,
    lastRefreshClarificationsTime: 0,
    openAnswerBoxClarification: undefined,
    isAnswerBoxLoading: false,
    isFilterLoading: false,
  });

  useEffect(() => {
    askDesktopNotificationPermission();
  }, []);

  useEffect(() => {
    if (status) {
      setState(prevState => ({ ...prevState, isFilterLoading: true }));
    }
  }, [status]);

  const render = () => {
    return (
      <ContentCard>
        <h3>Clarifications</h3>
        <hr />
        {renderCreateDialog()}
        {renderFilterWidget()}
        {renderClarifications()}
        {renderPagination()}
      </ContentCard>
    );
  };

  const refreshClarifications = async page => {
    const response = await dispatch(
      contestClarificationActions.getClarifications(contest.jid, status, statementLanguage, page)
    );
    setState(prevState => ({
      ...prevState,
      response,
      isAnswerBoxLoading: false,
      isFilterLoading: false,
    }));
    return response.data;
  };

  const renderClarifications = () => {
    const { response, openAnswerBoxClarification } = state;
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
          isAnswerBoxOpen={openAnswerBoxClarification === clarification}
          isAnswerBoxLoading={!!state.isAnswerBoxLoading}
          onToggleAnswerBox={toggleAnswerBox}
          onAnswerClarification={answerClarification}
        />
      </div>
    ));
  };

  const renderPagination = () => {
    const { lastRefreshClarificationsTime } = state;
    const key = '' + lastRefreshClarificationsTime + status;
    return <Pagination key={key} pageSize={PAGE_SIZE} onChangePage={onChangePage} />;
  };

  const toggleAnswerBox = clarification => {
    setState(prevState => ({ ...prevState, openAnswerBoxClarification: clarification }));
  };

  const onChangePage = async nextPage => {
    const data = await refreshClarifications(nextPage);
    return data.totalCount;
  };

  const renderCreateDialog = () => {
    const { response } = state;
    if (!response) {
      return null;
    }
    const { config } = response;
    if (!config.canCreate) {
      return null;
    }

    return (
      <ContestClarificationCreateDialog
        contest={contest}
        problemJids={config.problemJids}
        problemAliasesMap={response.problemAliasesMap}
        problemNamesMap={response.problemNamesMap}
        statementLanguage={statementLanguage}
        onCreateClarification={createClarification}
      />
    );
  };

  const createClarification = async (contestJid, data) => {
    await dispatch(contestClarificationActions.createClarification(contestJid, data));
    setState(prevState => ({ ...prevState, lastRefreshClarificationsTime: new Date().getTime() }));
  };

  const answerClarification = async (contestJid, clarificationJid, data) => {
    setState(prevState => ({ ...prevState, isAnswerBoxLoading: true }));
    try {
      await dispatch(contestClarificationActions.answerClarification(contestJid, clarificationJid, data));
      setState(prevState => ({ ...prevState, lastRefreshClarificationsTime: new Date().getTime() }));
      toggleAnswerBox();
    } catch (err) {
      // Don't close answer box yet on error
    } finally {
      setState(prevState => ({ ...prevState, isAnswerBoxLoading: false }));
    }
  };

  const renderFilterWidget = () => {
    const { response, isFilterLoading } = state;
    if (!response) {
      return null;
    }
    const { config } = response;
    const { canSupervise } = config;
    if (!canSupervise) {
      return null;
    }

    return (
      <ClarificationFilterWidget
        statuses={['ASKED']}
        status={status}
        onFilter={onFilter}
        isLoading={!!isFilterLoading}
      />
    );
  };

  const onFilter = async newFilter => {
    navigate({ search: stringify(newFilter) });
  };

  return render();
}

export default withBreadcrumb('Clarifications')(ContestClarificationsPage);
