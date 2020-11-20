import { Button, Intent } from '@blueprintjs/core';
import * as React from 'react';
import { Field, reduxForm } from 'redux-form';

import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { FormRichTextArea } from '../../../../../../components/forms/FormRichTextArea/FormRichTextArea';

function ContestEditDescriptionForm({ handleSubmit, submitting, onCancel }) {
  const descriptionField = {
    name: 'description',
    rows: 16,
    validate: [],
  };

  return (
    <form className="contest-edit-dialog__content" onSubmit={handleSubmit}>
      <Field component={FormRichTextArea} {...descriptionField} />
      <hr />
      <ActionButtons>
        <Button text="Cancel" disabled={submitting} onClick={onCancel} />
        <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
      </ActionButtons>
    </form>
  );
}

export default reduxForm({
  form: 'contest-description-edit',
  touchOnBlur: false,
})(ContestEditDescriptionForm);
