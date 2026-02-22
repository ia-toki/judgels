import { useMutation, useQuery } from '@tanstack/react-query';

import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { userQueryOptions } from '../../../../../modules/queries/user';
import { updateUserInfoMutationOptions, userInfoQueryOptions } from '../../../../../modules/queries/userInfo';
import { useSession } from '../../../../../modules/session';
import { InfoPanel } from '../../../panels/info/InfoPanel/InfoPanel';

import * as toastActions from '../../../../../modules/toast/toastActions';

export default function InfoPage() {
  const { user: sessionUser } = useSession();
  const userJid = sessionUser.jid;

  const { data: user } = useQuery(userQueryOptions(userJid));
  const { data: info } = useQuery(userInfoQueryOptions(userJid));

  const updateInfoMutation = useMutation(updateUserInfoMutationOptions(userJid));

  const onUpdateInfo = async infoData => {
    await updateInfoMutation.mutateAsync(infoData, {
      onSuccess: () => {
        toastActions.showSuccessToast('Info updated.');
      },
    });
  };

  if (!user || !info) {
    return <LoadingState />;
  }
  return <InfoPanel email={user.email} info={info} onUpdateInfo={onUpdateInfo} />;
}
