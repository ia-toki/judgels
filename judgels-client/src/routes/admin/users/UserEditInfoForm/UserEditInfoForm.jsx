import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { countriesData } from '../../../../assets/data/countries';
import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { FormTableSelect } from '../../../../components/forms/FormTableSelect/FormTableSelect';
import { FormTableTextInput } from '../../../../components/forms/FormTableTextInput/FormTableTextInput';
import { withSubmissionError } from '../../../../modules/form/submissionError';

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

export default function UserEditInfoForm({ onSubmit, initialValues, onCancel }) {
  const countryOptions = countriesData.map(country => (
    <option key={country.code} value={country.code}>
      {country.name}
    </option>
  ));

  return (
    <Form onSubmit={withSubmissionError(onSubmit)} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
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
            </tbody>
          </HTMLTable>
          <hr />
          <ActionButtons justifyContent="end">
            <Button text="Cancel" disabled={submitting} onClick={onCancel} />
            <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
          </ActionButtons>
        </form>
      )}
    </Form>
  );
}
