import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export function HomeSection({ home = { banner: '' } }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '300px' };

  const rows = [{ key: 'banner', title: 'Banner', value: <HtmlText>{home.banner || ''}</HtmlText> }];

  const updateSettings = async values => {
    await updateSettingsMutation.mutateAsync(
      { home: { banner: values.banner || '' } },
      {
        onSuccess: () => toastActions.showSuccessToast('Settings updated.'),
      }
    );
    setIsEditing(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small variant="outlined" icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = {
        banner: home.banner,
      };
      return (
        <Form onSubmit={updateSettings} initialValues={initialValues}>
          {({ handleSubmit, submitting }) => (
            <Flex asChild flexDirection="column" gap={2}>
              <form onSubmit={handleSubmit}>
                <Field component={FormRichTextArea} rows={10} name="banner" label="Banner" />
                <ActionButtons justifyContent="end">
                  <Button text="Cancel" disabled={submitting} onClick={() => setIsEditing(false)} />
                  <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
                </ActionButtons>
              </form>
            </Flex>
          )}
        </Form>
      );
    }

    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Home</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}
