import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Field, Form } from 'react-final-form';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../components/HtmlText/HtmlText';
import { FormRichTextArea } from '../../../../components/forms/FormRichTextArea/FormRichTextArea';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { updateSettingsMutationOptions } from '../../../../modules/queries/setting';

import * as toastActions from '../../../../modules/toast/toastActions';

export function HomeSection({ home = { banner: '' } }) {
  const updateSettingsMutation = useMutation(updateSettingsMutationOptions());

  const [isEditing, setIsEditing] = useState(false);

  const keyStyles = { width: '300px' };

  const renderEditButton = () => {
    if (isEditing) {
      return null;
    }
    return (
      <ActionButtons>
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditing(true)}>
          Edit
        </Button>
      </ActionButtons>
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
            <form onSubmit={handleSubmit}>
              <Field component={FormRichTextArea} rows={10} name="banner" label="Banner" />

              <hr />
              <ActionButtons justifyContent="end">
                <Button text="Cancel" disabled={submitting} onClick={() => setIsEditing(false)} />
                <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
              </ActionButtons>
            </form>
          )}
        </Form>
      );
    }

    const rows = [{ key: 'banner', title: 'Banner', value: <HtmlText>{home.banner || ''}</HtmlText> }];
    return <FormTable keyStyles={keyStyles} rows={rows} />;
  };

  const updateSettings = values => {
    updateSettingsMutation.mutate(
      { home: { banner: values.banner || '' } },
      {
        onSuccess: () => toastActions.showSuccessToast('Settings updated.'),
      }
    );
    setIsEditing(false);
  };

  return (
    <ContentCard title="Home settings">
      {renderEditButton()}
      {renderContent()}
    </ContentCard>
  );
}
