import { Button, Intent } from '@blueprintjs/core';
import * as CountryList from 'country-list';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { FormTableTextInput } from '../../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { Required } from '../../../../../components/forms/validations';
import { HorizontalDivider } from '../../../../../components/HorizontalDivider/HorizontalDivider';
import { HorizontalInnerDivider } from '../../../../../components/HorizontalInnerDivider/HorizontalInnerDivider';
import { ActionButtons } from '../../../../../components/ActionButtons/ActionButtons';
import { FormTableTextArea } from '../../../../../components/forms/FormTableTextArea/FormTableTextArea';
import { FormTableSelect } from '../../../../../components/forms/FormTableSelect/FormTableSelect';
import { UserProfile } from '../../../../../modules/api/jophiel/user';

const nameField: any = {
  name: 'name',
  label: 'Name',
  labelHelper: 'required',
  validate: [Required],
};

const genderField: any = {
  name: 'gender',
  label: 'Gender',
};

const nationalityField: any = {
  name: 'nationality',
  label: 'Nationality',
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
  name: 'institution',
  label: 'Name',
};

const countryField: any = {
  name: 'country',
  label: 'Country',
};

const provinceField: any = {
  name: 'province',
  label: 'Province/state',
};

const cityField: any = {
  name: 'city',
  label: 'City',
};

export interface ProfileFormProps extends InjectedFormProps<UserProfile> {
  onCancel: () => void;
}

const ProfileForm = (props: ProfileFormProps) => {
  const countryOptions = CountryList()
    .getNames()
    .map(name => (
      <option key={name} value={name}>
        {name}
      </option>
    ));

  return (
    <form onSubmit={props.handleSubmit}>
      <h4>My info</h4>
      <table className="pt-table pt-striped">
        <tbody>
          <Field component={FormTableTextInput} {...nameField} />
          <Field component={FormTableSelect} {...genderField}>
            <option />
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
          </Field>
          <Field component={FormTableSelect} {...nationalityField}>
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
      <table className="pt-table pt-striped">
        <tbody>
          <Field component={FormTableTextInput} {...institutionField} />
          <Field component={FormTableSelect} {...countryField}>
            <option />
            {countryOptions}
          </Field>
          <Field component={FormTableTextInput} {...provinceField} />
          <Field component={FormTableTextInput} {...cityField} />
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

export default reduxForm<UserProfile>({ form: 'profile' })(ProfileForm);
