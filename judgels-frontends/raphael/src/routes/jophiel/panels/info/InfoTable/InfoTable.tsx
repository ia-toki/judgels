import * as React from 'react';

import { UserInfo, userInfoGender } from '../../../../../modules/api/jophiel/userInfo';
import { FormTable, FormTableRow } from '../../../../../components/forms/FormTable/FormTable';
import { HorizontalInnerDivider } from '../../../../../components/HorizontalInnerDivider/HorizontalInnerDivider';

export interface InfoTableProps {
  info: UserInfo;
}

export const InfoTable = (props: InfoTableProps) => {
  const { info } = props;

  const infoRows: FormTableRow[] = [
    { key: 'name', title: 'Name', value: info.name },
    {
      key: 'gender',
      title: 'Gender',
      value: info.gender && userInfoGender[info.gender],
    },
    { key: 'nationality', title: 'Country', value: info.nationality },
    { key: 'homeAddress', title: 'Home address', value: info.homeAddress },
    { key: 'shirtSize', title: 'Shirt size', value: info.shirtSize },
  ];

  const institutionRows: FormTableRow[] = [
    { key: 'institution', title: 'Name', value: info.institution },
    { key: 'country', title: 'Country', value: info.country },
    {
      key: 'province',
      title: 'Province/State',
      value: info.province,
    },
    { key: 'city', title: 'City', value: info.city },
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
