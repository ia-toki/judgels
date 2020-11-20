import { Alignment, Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../../forms/validations';
import { FormTableSelect2 } from '../../forms/FormTableSelect2/FormTableSelect2';
import { statementLanguageDisplayNamesMap } from '../../../modules/api/sandalphon/language';

import './StatementLanguageForm.css';

function StatementLanguageForm({ handleSubmit, submitting, statementLanguages }) {
  const field = {
    className: 'form-statement-language',
    name: 'statementLanguage',
    optionValues: statementLanguages,
    optionNamesMap: statementLanguageDisplayNamesMap,
    validate: [Required],
  };

  return (
    <form onSubmit={handleSubmit}>
      <table className="statement-language-form__field">
        <tbody>
          <Field component={FormTableSelect2} {...field} />
        </tbody>
      </table>
      <Button
        className="statement-language-form__button"
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

export default reduxForm({ form: 'statement-language', touchOnBlur: false })(StatementLanguageForm);
