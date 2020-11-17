import * as React from 'react';

import { FormTable, FormTableRow } from '../../../../../components/forms/FormTable/FormTable';
import { HorizontalInnerDivider } from '../../../../../components/HorizontalInnerDivider/HorizontalInnerDivider';
import { UserInfo, userInfoGender } from '../../../../../modules/api/jophiel/userInfo';
import { getCountryName } from '../../../../../assets/data/countries';

export interface InfoTableProps {
  email: string;
  info: UserInfo;
}

export const InfoTable = (props: InfoTableProps) => {
  const { email, info } = props;

  const infoRows: FormTableRow[] = [
    { key: 'name', title: 'Name', value: info.name },
    { key: 'email', title: 'Email', value: email },
    {
      key: 'gender',
      title: 'Gender',
      value: info.gender && userInfoGender[info.gender],
    },
    { key: 'country', title: 'Country', value: getCountryName(info.country) },
    { key: 'homeAddress', title: 'Home address', value: info.homeAddress },
    { key: 'shirtSize', title: 'Shirt size', value: info.shirtSize },
  ];

  const institutionRows: FormTableRow[] = [
    { key: 'institutionName', title: 'Name', value: info.institutionName },
    { key: 'institutionCountry', title: 'Country', value: getCountryName(info.institutionCountry) },
    {
      key: 'institutionProvince',
      title: 'Province/State',
      value: info.institutionProvince,
    },
    { key: 'institutionCity', title: 'City', value: info.institutionCity },
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
