import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { countriesData } from '../../../../../assets/data/countries';
import { ActionButtons } from '../../../../../components/ActionButtons/ActionButtons';
import { HorizontalDivider } from '../../../../../components/HorizontalDivider/HorizontalDivider';
import { HorizontalInnerDivider } from '../../../../../components/HorizontalInnerDivider/HorizontalInnerDivider';
import { FormTableSelect } from '../../../../../components/forms/FormTableSelect/FormTableSelect';
import { FormTableTextArea } from '../../../../../components/forms/FormTableTextArea/FormTableTextArea';
import { FormTableTextInput } from '../../../../../components/forms/FormTableTextInput/FormTableTextInput';

const nameField = {
  name: 'name',
  label: 'Name',
};

const genderField = {
  name: 'gender',
  label: 'Gender',
};

const countryField = {
  name: 'country',
  label: 'Country',
};

const homeAddressField = {
  name: 'homeAddress',
  label: 'Home address',
};

const shirtSizeField = {
  name: 'shirtSize',
  label: 'Shirt size',
};

const institutionField = {
  name: 'institutionName',
  label: 'Name',
};

const institutionCountryField = {
  name: 'institutionCountry',
  label: 'Country',
};

const institutionProvinceField = {
  name: 'institutionProvince',
  label: 'Province/State',
};

const institutionCityField = {
  name: 'institutionCity',
  label: 'City',
};

export default function InfoForm({ onSubmit, initialValues, onCancel }) {
  const countryOptions = countriesData.map(country => (
    <option key={country.code} value={country.code}>
      {country.name}
    </option>
  ));

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <h4>My info</h4>
          <HTMLTable striped>
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
          </HTMLTable>

          <HorizontalInnerDivider />

          <h4>My institution (school/organization represented)</h4>
          <HTMLTable striped>
            <tbody>
              <Field component={FormTableTextInput} {...institutionField} />
              <Field component={FormTableSelect} {...institutionCountryField}>
                <option />
                {countryOptions}
              </Field>
              <Field component={FormTableTextInput} {...institutionProvinceField} />
              <Field component={FormTableTextInput} {...institutionCityField} />
            </tbody>
          </HTMLTable>

          <HorizontalDivider />

          <ActionButtons>
            <Button data-key="cancel" text="Cancel" onClick={onCancel} disabled={submitting} />
            <Button type="submit" text="Save changes" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
