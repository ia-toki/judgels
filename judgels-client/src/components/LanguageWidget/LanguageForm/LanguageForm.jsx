import { Alignment, Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { languageDisplayNamesMap } from '../../../modules/api/sandalphon/language';
import { FormTableSelect2 } from '../../forms/FormTableSelect2/FormTableSelect2';
import { Required } from '../../forms/validations';

import './LanguageForm.scss';

export default function LanguageForm({ onSubmit, initialValues, languages }) {
  const field = {
    className: 'form-language',
    name: 'language',
    optionValues: languages,
    optionNamesMap: languageDisplayNamesMap,
    validate: Required,
    disabled: languages.length <= 1,
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting, dirty }) => (
        <form onSubmit={handleSubmit} className="language-form">
          <table className="language-form__field">
            <tbody>
              <Field component={FormTableSelect2} {...field} />
            </tbody>
          </table>
          {languages.length > 1 && (
            <Button
              className="language-form__button"
              type="submit"
              text="Switch"
              alignText={Alignment.LEFT}
              intent={Intent.PRIMARY}
              loading={submitting}
              disabled={!dirty}
            />
          )}
        </form>
      )}
    </Form>
  );
}
