import * as React from 'react';

import { UserProfile, userProfileGender } from '../../../../../modules/api/jophiel/user';
import { FormTable, FormTableRow } from '../../../../../components/forms/FormTable/FormTable';
import { HorizontalInnerDivider } from '../../../../../components/HorizontalInnerDivider/HorizontalInnerDivider';

export interface ProfileTableProps {
  profile: UserProfile;
}

export const ProfileTable = (props: ProfileTableProps) => {
  const { profile } = props;

  const infoRows: FormTableRow[] = [
    { key: 'name', title: 'Name', value: profile.name },
    {
      key: 'gender',
      title: 'Gender',
      value: profile.gender && userProfileGender[profile.gender],
    },
    { key: 'nationality', title: 'Nationality', value: profile.nationality },
    { key: 'homeAddress', title: 'Home address', value: profile.homeAddress },
    { key: 'shirtSize', title: 'Shirt size', value: profile.shirtSize },
  ];

  const institutionRows: FormTableRow[] = [
    { key: 'institution', title: 'Name', value: profile.institution },
    { key: 'country', title: 'Country', value: profile.country },
    {
      key: 'province',
      title: 'Province/state',
      value: profile.province,
    },
    { key: 'city', title: 'City', value: profile.city },
  ];

  return (
    <div>
      <h4>My info</h4>
      <FormTable rows={infoRows} />

      <HorizontalInnerDivider />

      <h4>My institution</h4>
      <FormTable rows={institutionRows} />
    </div>
  );
};
