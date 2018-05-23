import { Alignment, Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, InjectedFormProps, reduxForm } from 'redux-form';

import { Required } from '../../forms/validations';
import { FormTableSelect2 } from '../../forms/FormTableSelect2/FormTableSelect2';
import { statementLanguageDisplayNamesMap } from '../../../modules/api/sandalphon/language';

import './StatementLanguageForm.css';

export interface StatementLanguageFormData {
  statementLanguage: string;
}

interface StatementLanguageFormProps extends InjectedFormProps<StatementLanguageFormData> {
  statementLanguages: string[];
}

const StatementLanguageForm = (props: StatementLanguageFormProps) => {
  const field: any = {
    name: 'statementLanguage',
    label: 'Language: ',
    optionValues: props.statementLanguages,
    optionNamesMap: statementLanguageDisplayNamesMap,
    validate: [Required],
  };

  return (
    <form onSubmit={props.handleSubmit}>
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
        loading={props.submitting}
      />
      <div className="clearfix" />
    </form>
  );
};

export default reduxForm<StatementLanguageFormData>({ form: 'statement-language' })(StatementLanguageForm);
