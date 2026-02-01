import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { useEffect, useState } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { Alias } from '../../../../components/forms/validations';
import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import ProblemSetProblemEditForm from '../ProblemSetProblemEditForm/ProblemSetProblemEditForm';
import { ProblemSetProblemsTable } from '../ProblemSetProblemsTable/ProblemSetProblemsTable';

export function ProblemSetProblemEditDialog({ isOpen, problemSet, onGetProblems, onSetProblems, onCloseDialog }) {
  const [state, setState] = useState({
    response: undefined,
    isEditing: false,
  });

  const refreshProblems = async () => {
    if (isOpen) {
      setState(prevState => ({ ...prevState, response: undefined }));
      const response = await onGetProblems(problemSet.jid);
      setState(prevState => ({ ...prevState, response }));
    }
  };

  useEffect(() => {
    refreshProblems();
  }, [problemSet]);

  const render = () => {
    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={closeDialog} title="Edit problemset problems" canOutsideClickClose={false}>
          {renderDialogContent()}
        </Dialog>
      </div>
    );
  };

  const closeDialog = () => {
    onCloseDialog();
    setState(prevState => ({ ...prevState, isEditing: false }));
  };

  const renderDialogContent = () => {
    const { response, isEditing } = state;
    if (!response) {
      return renderDialogForm(<LoadingState />, null);
    }

    if (isEditing) {
      const props = {
        validator: validateProblems,
        renderFormComponents: renderDialogForm,
        onSubmit: updateProblems,
        initialValues: { problems: serializeProblems(response.data, response.problemsMap, response.contestsMap) },
      };
      return <ProblemSetProblemEditForm {...props} />;
    } else {
      const content = <ProblemSetProblemsTable response={response} />;
      const submitButton = <Button data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={toggleEditing} />;
      return renderDialogForm(content, submitButton);
    }
  };

  const renderDialogForm = (content, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>
        {content}
        {renderInstructions()}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  const renderInstructions = () => {
    if (!state.isEditing) {
      return null;
    }

    return (
      <Callout icon={null}>
        <p>
          <strong>Format:</strong> <code>alias,slug[,type[,contestSlugs]]</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,hello\nB,tree,PROGRAMMING\nC,flow,BUNDLE,contest-1;contest-2'}</pre>
      </Callout>
    );
  };

  const toggleEditing = () => {
    setState(prevState => ({
      ...prevState,
      isEditing: !prevState.isEditing,
    }));
  };

  const updateProblems = async data => {
    const problems = deserializeProblems(data.problems);
    await onSetProblems(problemSet.jid, problems);
    await refreshProblems();
    toggleEditing();
  };

  const serializeProblems = (problems, problemsMap, contestsMap) => {
    return problems
      .map(p => {
        if (p.contestJids.length > 0) {
          const contestSlugs = p.contestJids
            .map(jid => contestsMap[jid])
            .filter(c => c)
            .map(c => c.slug)
            .join(';');
          return `${p.alias},${problemsMap[p.problemJid].slug},${p.type},${contestSlugs}`;
        } else if (p.type !== ProblemType.Programming) {
          return `${p.alias},${problemsMap[p.problemJid].slug},${p.type}`;
        } else {
          return `${p.alias},${problemsMap[p.problemJid].slug}`;
        }
      })
      .join('\n');
  };

  const deserializeProblems = problems => {
    return problems
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(s => ({
        alias: s[0],
        slug: s[1],
        type: s[2] || ProblemType.Programming,
        contestSlugs: (s[3] || '')
          .split(';')
          .filter(slug => slug)
          .map(slug => slug.trim()),
      }));
  };

  const validateProblems = value => {
    const problems = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases = [];
    const slugs = [];

    for (const p of problems) {
      if (p.length < 2 || p.length > 4) {
        return 'Each line must contain 2-4 comma-separated elements';
      }
      const alias = p[0];
      const aliasValidation = Alias(alias);
      if (aliasValidation) {
        return 'Problem aliases: ' + aliasValidation;
      }

      const slug = p[1];

      aliases.push(alias);
      slugs.push(slug);
    }

    if (new Set(aliases).size !== aliases.length) {
      return 'Problem aliases must be unique';
    }
    if (new Set(slugs).size !== slugs.length) {
      return 'Problem slugs must be unique';
    }

    return undefined;
  };

  return render();
}
