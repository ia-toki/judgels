import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { userQueryOptions } from '../../../../modules/queries/user';
import { updateUserInfoMutationOptions, userInfoQueryOptions } from '../../../../modules/queries/userInfo';
import UserEditInfoForm from '../UserEditInfoForm/UserEditInfoForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export default function UserViewPage() {
  const { userJid } = useParams({ strict: false });

  const { data: user } = useSuspenseQuery(userQueryOptions(userJid));
  const { data: userInfo } = useSuspenseQuery(userInfoQueryOptions(userJid));

  const updateUserInfoMutation = useMutation(updateUserInfoMutationOptions(userJid));

  const [isEditingInfo, setIsEditingInfo] = useState(false);

  const keyStyles = { width: '200px' };

  const generalRows = [
    { key: 'jid', title: 'JID', value: user.jid },
    { key: 'email', title: 'Email', value: user.email },
  ];

  const renderGeneralSection = () => {
    return (
      <div>
        <h4>General</h4>
        <FormTable keyStyles={keyStyles} rows={generalRows} />
      </div>
    );
  };

  const infoRows = [
    { key: 'name', title: 'Name', value: userInfo.name || '-' },
    { key: 'gender', title: 'Gender', value: userInfo.gender || '-' },
    { key: 'country', title: 'Country', value: userInfo.country || '-' },
  ];

  const updateUserInfo = data => {
    updateUserInfoMutation.mutate(data, {
      onSuccess: () => toastActions.showSuccessToast('User info updated.'),
    });
    setIsEditingInfo(false);
  };

  const renderInfoEditButton = () => {
    return (
      !isEditingInfo && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditingInfo(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderInfoSection = () => {
    const renderInfoContent = () => {
      if (isEditingInfo) {
        const initialValues = {
          name: userInfo.name || '',
          gender: userInfo.gender || '',
          country: userInfo.country || '',
        };
        return (
          <UserEditInfoForm
            initialValues={initialValues}
            onSubmit={updateUserInfo}
            onCancel={() => setIsEditingInfo(false)}
          />
        );
      }
      return <FormTable keyStyles={keyStyles} rows={infoRows} />;
    };

    return (
      <div>
        <Flex asChild justifyContent="space-between" alignItems="baseline">
          <h4>
            <span>Info</span>
            {renderInfoEditButton()}
          </h4>
        </Flex>
        {renderInfoContent()}
      </div>
    );
  };

  return (
    <ContentCard title={`Users › ${user.username}`}>
      {renderGeneralSection()}
      <hr />
      {renderInfoSection()}
    </ContentCard>
  );
}
