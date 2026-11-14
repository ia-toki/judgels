import { Button, Intent, Tag } from '@blueprintjs/core';
import { Tick, TrendingUp } from '@blueprintjs/icons';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContestContestantState } from '../../../../../modules/api/contestContestant';
import { contestBySlugQueryOptions } from '../../../../../modules/queries/contest';
import {
  myContestantStateQueryOptions,
  registerMyselfAsContestantMutationOptions,
  unregisterMyselfAsContestantMutationOptions,
} from '../../../../../modules/queries/contestContestant';
import { useSession } from '../../../../../modules/session';

import * as toastActions from '../../../../../modules/toast/toastActions';

export default function ContestRegistrationActions() {
  const { contestSlug } = useParams({ strict: false });
  const { isLoggedIn } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: contestantState } = useQuery({
    ...myContestantStateQueryOptions(contest.jid),
    enabled: isLoggedIn,
  });

  const registerMutation = useMutation(registerMyselfAsContestantMutationOptions(contest.jid));
  const unregisterMutation = useMutation(unregisterMyselfAsContestantMutationOptions(contest.jid));

  const register = () => {
    registerMutation.mutate(undefined, {
      onSuccess: () => toastActions.showSuccessToast('Successfully registered to the contest.'),
    });
  };

  const unregister = () => {
    unregisterMutation.mutate(undefined, {
      onSuccess: () => toastActions.showSuccessToast('Successfully unregistered from the contest.'),
    });
  };

  const renderRegisteredTag = () => {
    return (
      <Tag intent={Intent.SUCCESS} icon={<Tick />}>
        Registered
      </Tag>
    );
  };

  switch (contestantState) {
    case ContestContestantState.RegistrableWrongDivision:
      return (
        <Button small intent={Intent.WARNING} text="Your rating is not allowed for this contest division" disabled />
      );
    case ContestContestantState.Registrable:
      return (
        <Button
          small
          intent={Intent.PRIMARY}
          icon={<TrendingUp />}
          text="Register"
          onClick={register}
          loading={registerMutation.isPending}
        />
      );
    case ContestContestantState.Registrant:
      return (
        <>
          {renderRegisteredTag()}
          <Button
            small
            intent={Intent.DANGER}
            text="Unregister"
            onClick={unregister}
            loading={unregisterMutation.isPending}
          />
        </>
      );
    case ContestContestantState.Contestant:
      return renderRegisteredTag();
    default:
      return null;
  }
}
