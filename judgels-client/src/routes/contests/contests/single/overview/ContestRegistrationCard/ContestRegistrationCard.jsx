import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import { BanCircle, People, Tick } from '@blueprintjs/icons';
import { useQueryClient, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import { callAction } from '../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { useSession } from '../../../../../../modules/session';
import ContestRegistrantsDialog from '../ContestRegistrantsDialog/ContestRegistrantsDialog';

import * as contestContestantActions from '../../modules/contestContestantActions';

import './ContestRegistrationCard.scss';

export default function ContestRegistrationCard() {
  const { contestSlug } = useParams({ strict: false });
  const { isLoggedIn } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const queryClient = useQueryClient();

  const [state, setState] = useState({
    contestantState: undefined,
    contestantsCount: undefined,
    isActionButtonLoading: false,
    isRegistrantsDialogOpen: false,
  });

  const render = () => {
    if (!isLoggedIn) {
      return (
        <Callout icon={<BanCircle />} className="contest-registration-card--error secondary-info">
          Please log in to register.
        </Callout>
      );
    }

    return (
      <Callout className="contest-registration-card" icon={null}>
        {renderCard()}
      </Callout>
    );
  };

  const refresh = async () => {
    if (!isLoggedIn) {
      return;
    }

    const [contestantState, contestantsCount] = await Promise.all([
      callAction(contestContestantActions.getMyContestantState(contest.jid)),
      callAction(contestContestantActions.getApprovedContestantsCount(contest.jid)),
      queryClient.invalidateQueries({ queryKey: ['contest-by-slug', contestSlug, 'web-config'] }),
    ]);
    setState(prevState => ({ ...prevState, contestantState, contestantsCount }));
  };

  useEffect(() => {
    refresh();
  }, []);

  const renderCard = () => {
    const { contestantState, contestantsCount } = state;
    if (!contestantState || contestantsCount === undefined) {
      return <LoadingState />;
    }

    return (
      <>
        {renderContestantStateTag(contestantState)}
        {renderActionButton(contestantState)}
        {renderViewRegistrantsButton(contestantsCount)}
        {renderRegistrantsDialog()}
        <div className="clearfix" />
      </>
    );
  };

  const renderContestantStateTag = contestantState => {
    if (
      contestantState === ContestContestantState.Registrant ||
      contestantState === ContestContestantState.Contestant
    ) {
      return (
        <Tag large intent={Intent.SUCCESS} className="contest-registration-card__item contest-registration-card__state">
          <Tick /> Registered
        </Tag>
      );
    }
    return null;
  };

  const renderActionButton = contestantState => {
    if (contestantState === ContestContestantState.RegistrableWrongDivision) {
      return (
        <Button
          className="contest-registration-card__item contest-registration-card__action"
          intent={Intent.WARNING}
          text="Your rating is not allowed for this contest division"
          disabled
        />
      );
    }
    if (contestantState === ContestContestantState.Registrable) {
      return (
        <Button
          className="contest-registration-card__item contest-registration-card__action"
          intent={Intent.PRIMARY}
          text="Register"
          onClick={register}
          loading={state.isActionButtonLoading}
        />
      );
    }
    if (contestantState === ContestContestantState.Registrant) {
      return (
        <Button
          className="contest-registration-card__item contest-registration-card__action"
          intent={Intent.DANGER}
          text="Unregister"
          onClick={unregister}
          loading={state.isActionButtonLoading}
        />
      );
    }
    return null;
  };

  const renderViewRegistrantsButton = contestantsCount => {
    return (
      <Button
        className="contest-registration-card__item"
        icon={<People />}
        text={`View registrants (${contestantsCount})`}
        onClick={toggleRegistrantsDialog}
      />
    );
  };

  const renderRegistrantsDialog = () => {
    if (!state.isRegistrantsDialogOpen) {
      return null;
    }
    return <ContestRegistrantsDialog onClose={toggleRegistrantsDialog} />;
  };

  const register = async () => {
    setState(prevState => ({ ...prevState, isActionButtonLoading: true }));
    await callAction(contestContestantActions.registerMyselfAsContestant(contest.jid));
    setState(prevState => ({ ...prevState, isActionButtonLoading: false }));
    await refresh();
  };

  const unregister = async () => {
    setState(prevState => ({ ...prevState, isActionButtonLoading: true }));
    await callAction(contestContestantActions.unregisterMyselfAsContestant(contest.jid));
    setState(prevState => ({ ...prevState, isActionButtonLoading: false }));
    await refresh();
  };

  const toggleRegistrantsDialog = () => {
    setState(prevState => ({
      ...prevState,
      isRegistrantsDialogOpen: !prevState.isRegistrantsDialogOpen,
    }));
  };

  return render();
}
