import { Intent } from '@blueprintjs/core';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { allModules } from '../../../../../../modules/api/uriel/contestModule';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { ContestModuleCard } from '../ContestModuleCard/ContestModuleCard';

import * as contestModuleActions from '../../modules/contestModuleActions';
import * as contestWebActions from '../../modules/contestWebActions';

export default function ContestEditModulesTab() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();

  const [state, setState] = useState({
    modules: undefined,
  });

  const refreshModules = async () => {
    const modules = await dispatch(contestModuleActions.getModules(contest.jid));
    setState(prevState => ({ ...prevState, modules }));
  };

  useEffect(() => {
    refreshModules();
  }, []);

  const render = () => {
    return (
      <>
        <h4>Modules settings</h4>
        <hr />
        {renderContent()}
      </>
    );
  };

  const renderContent = () => {
    const { modules } = state;

    if (!modules) {
      return <LoadingState />;
    }

    const enabledModules = allModules.filter(m => modules.indexOf(m) !== -1);
    const disabledModules = allModules.filter(m => modules.indexOf(m) === -1);

    return (
      <div className="contest-edit-dialog__content">
        <>{renderEnabledModules(enabledModules)}</>
        <hr />
        <>{renderDisabledModules(disabledModules)}</>
      </div>
    );
  };

  const renderEnabledModules = enabledModules => {
    if (enabledModules.length === 0) {
      return (
        <p>
          <small>No enabled modules.</small>
        </p>
      );
    }

    return enabledModules.map(module => (
      <ContestModuleCard
        key={module}
        type={module}
        intent={Intent.PRIMARY}
        buttonIntent={Intent.NONE}
        buttonText={'Disable'}
        buttonOnClick={disableModule}
        buttonIsLoading={false}
        buttonIsDisabled={false}
      />
    ));
  };

  const renderDisabledModules = disabledModules => {
    if (disabledModules.length === 0) {
      return (
        <p>
          <small>No disabled modules.</small>
        </p>
      );
    }

    return disabledModules.map(module => (
      <ContestModuleCard
        key={module}
        type={module}
        intent={Intent.NONE}
        buttonIntent={Intent.PRIMARY}
        buttonText={'Enable'}
        buttonOnClick={enableModule}
        buttonIsLoading={false}
        buttonIsDisabled={false}
      />
    ));
  };

  const enableModule = async type => {
    await dispatch(contestModuleActions.enableModule(contest.jid, type));
    await Promise.all([dispatch(contestWebActions.getContestByJidWithWebConfig(contest.jid)), refreshModules()]);
  };

  const disableModule = async type => {
    await dispatch(contestModuleActions.disableModule(contest.jid, type));
    await Promise.all([dispatch(contestWebActions.getContestByJidWithWebConfig(contest.jid)), refreshModules()]);
  };

  return render();
}
