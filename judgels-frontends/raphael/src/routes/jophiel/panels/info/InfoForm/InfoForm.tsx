import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTableTextInput } from 'components/forms/FormTableTextInput/FormTableTextInput';
import { HorizontalDivider } from 'components/HorizontalDivider/HorizontalDivider';
import { HorizontalInnerDivider } from 'components/HorizontalInnerDivider/HorizontalInnerDivider';
import { ActionButtons } from 'components/ActionButtons/ActionButtons';
import { FormTableTextArea } from 'components/forms/FormTableTextArea/FormTableTextArea';
import { FormTableSelect } from 'components/forms/FormTableSelect/FormTableSelect';
import { countriesData } from 'assets/data/countries';
import { UserInfo } from 'modules/api/jophiel/userInfo';

const nameField: any = {
  name: 'name',
  label: 'Name',
};

const genderField: any = {
  name: 'gender',
  label: 'Gender',
};

const countryField: any = {
  name: 'country',
  label: 'Country',
};

const homeAddressField: any = {
  name: 'homeAddress',
  label: 'Home address',
};

const shirtSizeField: any = {
  name: 'shirtSize',
  label: 'Shirt size',
};

const institutionField: any = {
  name: 'institutionName',
  label: 'Name',
};

const institutionCountryField: any = {
  name: 'institutionCountry',
  label: 'Country',
};

const institutionProvinceField: any = {
  name: 'institutionProvince',
  label: 'Province/State',
};

const institutionCityField: any = {
  name: 'institutionCity',
  label: 'City',
};

export interface InfoFormProps extends InjectedFormProps<UserInfo> {
  onCancel: () => void;
}

const InfoForm = (props: InfoFormProps) => {
  const countryOptions = countriesData.map(country => (
    <option key={country.code} value={country.code}>
      {country.name}
    </option>
  ));

  return (
    <form onSubmit={props.handleSubmit}>
      <h4>My info</h4>
      <table className="bp3-html-table bp3-html-table-striped">
        <tbody>
          <Field component={FormTableTextInput} {...nameField} />
          <Field component={FormTableSelect} {...genderField}>
            <option />
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </Field>
          <Field component={FormTableSelect} {...countryField}>
            <option />
            {countryOptions}
          </Field>
          <Field component={FormTableTextArea} {...homeAddressField} />
          <Field component={FormTableSelect} {...shirtSizeField}>
            <option />
            <option value="XXS">XXS</option>
            <option value="XS">XS</option>
            <option value="S">S</option>
            <option value="M">M</option>
            <option value="L">L</option>
            <option value="XL">XL</option>
            <option value="XXL">XXL</option>
            <option value="XXXL">XXXL</option>
          </Field>
        </tbody>
      </table>

      <HorizontalInnerDivider />

      <h4>My institution (school/organization represented)</h4>
      <table className="bp3-html-table bp3-html-table-striped">
        <tbody>
          <Field component={FormTableTextInput} {...institutionField} />
          <Field component={FormTableSelect} {...institutionCountryField}>
            <option />
            {countryOptions}
          </Field>
          <Field component={FormTableTextInput} {...institutionProvinceField} />
          <Field component={FormTableTextInput} {...institutionCityField} />
        </tbody>
      </table>

      <HorizontalDivider />

      <ActionButtons>
        <Button type="submit" text="Save changes" intent={Intent.PRIMARY} loading={props.submitting} />
        <Button data-key="cancel" text="Cancel" onClick={props.onCancel} disabled={props.submitting} />
      </ActionButtons>
    </form>
  );
};

export default reduxForm<UserInfo>({ form: 'info' })(InfoForm);
