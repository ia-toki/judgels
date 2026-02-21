import { Button, Callout, Intent, Tag } from '@blueprintjs/core';
import { BanCircle, People, Tick } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ContestContestantState } from '../../../../../../modules/api/uriel/contestContestant';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import {
  approvedContestantsCountQueryOptions,
  myContestantStateQueryOptions,
  registerMyselfMutationOptions,
  unregisterMyselfMutationOptions,
} from '../../../../../../modules/queries/contestContestant';
import { useSession } from '../../../../../../modules/session';
import ContestRegistrantsDialog from '../ContestRegistrantsDialog/ContestRegistrantsDialog';

import * as toastActions from '../../../../../../modules/toast/toastActions';

import './ContestRegistrationCard.scss';

export default function ContestRegistrationCard() {
  const { contestSlug } = useParams({ strict: false });
  const { isLoggedIn } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: contestantState } = useQuery({
    ...myContestantStateQueryOptions(contest.jid),
    enabled: isLoggedIn,
  });

  const { data: contestantsCount } = useQuery({
    ...approvedContestantsCountQueryOptions(contest.jid),
    enabled: isLoggedIn,
  });

  const registerMutation = useMutation(registerMyselfMutationOptions(contest.jid));
  const unregisterMutation = useMutation(unregisterMyselfMutationOptions(contest.jid));

  const [isRegistrantsDialogOpen, setIsRegistrantsDialogOpen] = useState(false);

  if (!isLoggedIn) {
    return (
      <Callout icon={<BanCircle />} className="contest-registration-card--error secondary-info">
        Please log in to register.
      </Callout>
    );
  }

  const renderCard = () => {
    if (contestantState === undefined || contestantsCount === undefined) {
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
          loading={registerMutation.isPending}
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
          loading={unregisterMutation.isPending}
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
        onClick={() => setIsRegistrantsDialogOpen(true)}
      />
    );
  };

  const renderRegistrantsDialog = () => {
    if (!isRegistrantsDialogOpen) {
      return null;
    }
    return <ContestRegistrantsDialog onClose={() => setIsRegistrantsDialogOpen(false)} />;
  };

  const register = async () => {
    await registerMutation.mutateAsync(undefined, {
      onSuccess: () => toastActions.showSuccessToast('Successfully registered to the contest.'),
    });
  };

  const unregister = async () => {
    await unregisterMutation.mutateAsync(undefined, {
      onSuccess: () => toastActions.showSuccessToast('Successfully unregistered from the contest.'),
    });
  };

  return (
    <Callout className="contest-registration-card" icon={null}>
      {renderCard()}
    </Callout>
  );
}
