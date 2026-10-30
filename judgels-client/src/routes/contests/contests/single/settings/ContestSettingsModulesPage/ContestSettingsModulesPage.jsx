import { Button, Card, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useState } from 'react';
import { Form } from 'react-final-form';

import { ActionButtons } from '../../../../../../components/ActionButtons/ActionButtons';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { FormTable } from '../../../../../../components/forms/FormTable/FormTable';
import { allModules, moduleDescriptionsMap, moduleTitlesMap } from '../../../../../../modules/api/contestModule';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import {
  contestModulesConfigQueryOptions,
  contestModulesQueryOptions,
  disableContestModuleMutationOptions,
  enableContestModuleMutationOptions,
  upsertContestModulesConfigMutationOptions,
} from '../../../../../../modules/queries/contestModule';
import { ContestModule } from '../ContestModule/ContestModule';
import { moduleConfigs, scoreboardConfig, styleConfigs } from '../modules/contestModuleConfigs';

import * as toastActions from '../../../../../../modules/toast/toastActions';

export default function ContestSettingsModulesPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));

  const { data: modules } = useQuery(contestModulesQueryOptions(contest.jid));
  const { data: config } = useQuery(contestModulesConfigQueryOptions(contest.jid));

  const enableModuleMutation = useMutation(enableContestModuleMutationOptions(contest.jid));
  const disableModuleMutation = useMutation(disableContestModuleMutationOptions(contest.jid));
  const upsertConfigMutation = useMutation(upsertContestModulesConfigMutationOptions(contest.jid));

  // Set while editing; kept in state so that the form is not reset when the page rerenders.
  const [initialValues, setInitialValues] = useState(null);
  const isEditing = initialValues !== null;

  if (!modules || !config) {
    return <LoadingState />;
  }

  // The style and scoreboard modules are always enabled, so they come first and cannot be disabled.
  const enabledModules = [
    { key: 'style', title: `${contest.style} style`, spec: styleConfigs[contest.style] },
    { key: 'scoreboard', title: 'Scoreboard', spec: scoreboardConfig },
    ...allModules.filter(type => modules.includes(type)).map(type => ({ key: type, type, spec: moduleConfigs[type] })),
  ];
  const disabledModules = allModules.filter(type => !modules.includes(type));

  const configSpecs = enabledModules.map(m => m.spec).filter(spec => spec && config[spec.key]);

  const startEditing = () => {
    setInitialValues(Object.fromEntries(configSpecs.map(spec => [spec.key, spec.toFormValues(config[spec.key])])));
  };

  const upsertConfig = async values => {
    const newConfig = Object.fromEntries(configSpecs.map(spec => [spec.key, spec.toConfig(values[spec.key] || {})]));
    await upsertConfigMutation.mutateAsync(newConfig, {
      onSuccess: () => toastActions.showSuccessToast('Configs updated.'),
    });
    setInitialValues(null);
  };

  const renderToggleButton = type => {
    const isEnabled = modules.includes(type);
    const mutation = isEnabled ? disableModuleMutation : enableModuleMutation;
    return (
      <Button
        small
        intent={isEnabled ? Intent.NONE : Intent.PRIMARY}
        disabled={isEditing}
        loading={mutation.isPending && mutation.variables === type}
        onClick={() => mutation.mutate(type)}
      >
        {isEnabled ? 'Disable' : 'Enable'}
      </Button>
    );
  };

  const renderConfigView = spec => {
    if (spec.renderView) {
      return spec.renderView(config[spec.key], config.profilesMap);
    }
    return <FormTable rows={spec.rows(config[spec.key])} />;
  };

  const renderConfigFields = (spec, values) => {
    const name = field => `${spec.key}.${field}`;
    const moduleValues = values[spec.key] || {};
    if (spec.renderFields) {
      return spec.renderFields(moduleValues, name);
    }
    return (
      <HTMLTable striped>
        <tbody>{spec.fields(moduleValues, name)}</tbody>
      </HTMLTable>
    );
  };

  const renderEnabledModule = ({ key, type, title, spec }, values) => {
    const hasConfig = spec && config[spec.key];
    return (
      <ContestModule
        key={key}
        title={title || moduleTitlesMap[type]}
        description={type && moduleDescriptionsMap[type]}
        action={type && renderToggleButton(type)}
      >
        {hasConfig && (isEditing ? renderConfigFields(spec, values) : renderConfigView(spec))}
      </ContestModule>
    );
  };

  const renderEnabledModulesCard = (values, actions) => (
    <Flex asChild flexDirection="column" gap={8}>
      <Card compact>
        {enabledModules.map(m => renderEnabledModule(m, values))}
        {actions}
      </Card>
    </Flex>
  );

  const renderEnabledModules = () => {
    if (!isEditing) {
      return renderEnabledModulesCard();
    }
    return (
      <Form onSubmit={upsertConfig} initialValues={initialValues}>
        {({ handleSubmit, values, submitting }) => (
          <form onSubmit={handleSubmit}>
            {renderEnabledModulesCard(
              values,
              <ActionButtons justifyContent="end">
                <Button text="Cancel" disabled={submitting} onClick={() => setInitialValues(null)} />
                <Button type="submit" text="Save" intent={Intent.PRIMARY} loading={submitting} />
              </ActionButtons>
            )}
          </form>
        )}
      </Form>
    );
  };

  const renderDisabledModules = () => {
    if (disabledModules.length === 0) {
      return (
        <p>
          <small>No disabled modules.</small>
        </p>
      );
    }
    return (
      <Flex flexDirection="column" gap={2}>
        {disabledModules.map(type => (
          <Card key={type} compact>
            <ContestModule
              title={moduleTitlesMap[type]}
              description={moduleDescriptionsMap[type]}
              action={renderToggleButton(type)}
            />
          </Card>
        ))}
      </Flex>
    );
  };

  return (
    <Flex flexDirection="column" gap={8}>
      <div>
        <Flex asChild gap={2} alignItems="center">
          <h4>
            <span>Enabled modules</span>
            {!isEditing && (
              <Button small variant="outlined" icon={<Edit />} onClick={startEditing}>
                Edit
              </Button>
            )}
          </h4>
        </Flex>
        {renderEnabledModules()}
      </div>
      <div>
        <h4>Disabled modules</h4>
        {renderDisabledModules()}
      </div>
    </Flex>
  );
}
