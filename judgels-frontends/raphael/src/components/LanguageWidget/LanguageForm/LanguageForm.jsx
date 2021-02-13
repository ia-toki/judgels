import { Alignment, Button, Intent } from '@blueprintjs/core';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../../forms/validations';
import { FormTableSelect2 } from '../../forms/FormTableSelect2/FormTableSelect2';
import { languageDisplayNamesMap } from '../../../modules/api/sandalphon/language';

import './LanguageForm.css';

function LanguageForm({ handleSubmit, submitting, languages }) {
  const field = {
    className: 'form-language',
    name: 'language',
    optionValues: languages,
    optionNamesMap: languageDisplayNamesMap,
    validate: [Required],
  };

  return (
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
  );
}

export default reduxForm({ touchOnBlur: false })(LanguageForm);
