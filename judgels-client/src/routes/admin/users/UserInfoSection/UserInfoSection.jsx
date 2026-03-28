import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { getCountryName } from '../../../../assets/data/countries';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { userInfoGender } from '../../../../modules/api/jophiel/userInfo';
import { updateUserInfoMutationOptions, userInfoQueryOptions } from '../../../../modules/queries/userInfo';
import UserInfoEditForm from '../UserInfoEditForm/UserInfoEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function UserInfoSection({ user }) {
  const { data: userInfo } = useSuspenseQuery(userInfoQueryOptions(user.jid));
  const updateUserInfoMutation = useMutation(updateUserInfoMutationOptions(user.jid));

  const [isEditing, setIsEditingInfo] = useState(false);

  const keyStyles = { width: '250px' };

  const infoRows = [
    { key: 'name', title: 'Name', value: userInfo.name },
    { key: 'gender', title: 'Gender', value: userInfo.gender && userInfoGender[userInfo.gender] },
    { key: 'country', title: 'Country', value: getCountryName(userInfo.country) },
    { key: 'homeAddress', title: 'Home address', value: userInfo.homeAddress },
    { key: 'shirtSize', title: 'Shirt size', value: userInfo.shirtSize },
    { key: 'institutionName', title: 'Institution name', value: userInfo.institutionName },
    { key: 'institutionCountry', title: 'Institution country', value: getCountryName(userInfo.institutionCountry) },
    { key: 'institutionProvince', title: 'Institution province/state', value: userInfo.institutionProvince },
    { key: 'institutionCity', title: 'Institution city', value: userInfo.institutionCity },
  ];

  const updateUserInfo = data => {
    updateUserInfoMutation.mutate(data, {
      onSuccess: () => toastActions.showSuccessToast('User info updated.'),
    });
    setIsEditingInfo(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditingInfo(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        name: userInfo.name || '',
        gender: userInfo.gender || '',
        country: userInfo.country || '',
        homeAddress: userInfo.homeAddress || '',
        shirtSize: userInfo.shirtSize || '',
        institutionName: userInfo.institutionName || '',
        institutionCountry: userInfo.institutionCountry || '',
        institutionProvince: userInfo.institutionProvince || '',
        institutionCity: userInfo.institutionCity || '',
      };
      return (
        <UserInfoEditForm
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
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
