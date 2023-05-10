import { Alignment, Button, Intent } from '@blueprintjs/core';
import { Field, Form } from 'react-final-form';

import { Required } from '../../forms/validations';
import { FormTableSelect2 } from '../../forms/FormTableSelect2/FormTableSelect2';
import { languageDisplayNamesMap } from '../../../modules/api/sandalphon/language';

import './LanguageForm.scss';

export default function LanguageForm({ onSubmit, initialValues, languages }) {
  const field = {
    className: 'form-language',
    name: 'language',
    optionValues: languages,
    optionNamesMap: languageDisplayNamesMap,
    validate: Required,
  };

  return (
    <Form onSubmit={onSubmit} initialValues={initialValues}>
      {({ handleSubmit, submitting }) => (
        <form onSubmit={handleSubmit}>
          <table className="language-form__field">
            <tbody>
              <Field component={FormTableSelect2} {...field} />
            </tbody>
          </table>
          <Button
            className="language-form__button"
            type="submit"
            text="Switch"
            alignText={Alignment.LEFT}
            intent={Intent.PRIMARY}
            loading={submitting}
          />
          <div className="clearfix" />
        </form>
      )}
    </Form>
  );
}
