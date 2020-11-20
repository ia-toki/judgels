import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { Required } from '../../../../../../components/forms/validations';
import { FormTextInput } from '../../../../../../components/forms/FormTextInput/FormTextInput';
import { FormSelect2 } from '../../../../../../components/forms/FormSelect2/FormSelect2';
import { FormTextArea } from '../../../../../../components/forms/FormTextArea/FormTextArea';
import { constructProblemName } from '../../../../../../modules/api/sandalphon/problem';

function ContestClarificationCreateForm({
  handleSubmit,
  submitting,
  contestJid,
  problemJids,
  problemAliasesMap,
  problemNamesMap,
  renderFormComponents,
}) {
  const topicField = {
    name: 'topicJid',
    label: 'Topic',
    validate: [Required],
    optionValues: [contestJid, ...problemJids],
    optionNamesMap: {
      [contestJid]: '(General)',
      ...Object.assign(
        {},
        ...problemJids.map(jid => ({
          [jid]: constructProblemName(problemNamesMap[jid], problemAliasesMap[jid]),
        }))
      ),
    },
  };

  const titleField = {
    name: 'title',
    label: 'Title',
    validate: [Required],
  };

  const questionField = {
    name: 'question',
    label: 'Question',
    rows: 5,
    validate: [Required],
  };

  const fields = (
    <>
      <Field component={FormSelect2} {...topicField} />
      <Field component={FormTextInput} {...titleField} />
      <Field component={FormTextArea} {...questionField} />
    </>
  );

  const submitButton = <Button type="submit" text="Submit" intent={Intent.PRIMARY} loading={submitting} />;

  return <form onSubmit={handleSubmit}>{renderFormComponents(fields, submitButton)}</form>;
}

export default reduxForm({
  form: 'contest-clarification-create',
  touchOnBlur: false,
})(ContestClarificationCreateForm);
