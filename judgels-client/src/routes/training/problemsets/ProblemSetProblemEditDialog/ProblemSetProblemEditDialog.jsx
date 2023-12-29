import { Button, Callout, Classes, Dialog, Intent } from '@blueprintjs/core';
import { Component } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { Alias } from '../../../../components/forms/validations';
import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import ProblemSetProblemEditForm from '../ProblemSetProblemEditForm/ProblemSetProblemEditForm';
import { ProblemSetProblemsTable } from '../ProblemSetProblemsTable/ProblemSetProblemsTable';

export class ProblemSetProblemEditDialog extends Component {
  state = {
    response: undefined,
    isEditing: false,
  };

  componentDidMount() {
    this.refreshProblems();
  }

  async componentDidUpdate(prevProps) {
    if (prevProps.problemSet !== this.props.problemSet) {
      this.refreshProblems();
    }
  }

  render() {
    const { isOpen } = this.props;
    return (
      <div className="content-card__section">
        <Dialog
          isOpen={isOpen}
          onClose={this.closeDialog}
          title="Edit problemset problems"
          canOutsideClickClose={false}
        >
          {this.renderDialogContent()}
        </Dialog>
      </div>
    );
  }

  closeDialog = () => {
    this.props.onCloseDialog();
    this.setState({ isEditing: false });
  };

  renderDialogContent = () => {
    const { response, isEditing } = this.state;
    if (!response) {
      return this.renderDialogForm(<LoadingState />, null);
    }

    if (isEditing) {
      const props = {
        validator: this.validateProblems,
        renderFormComponents: this.renderDialogForm,
        onSubmit: this.updateProblems,
        initialValues: { problems: this.serializeProblems(response.data, response.problemsMap, response.contestsMap) },
      };
      return <ProblemSetProblemEditForm {...props} />;
    } else {
      const content = <ProblemSetProblemsTable response={response} />;
      const submitButton = <Button data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={this.toggleEditing} />;
      return this.renderDialogForm(content, submitButton);
    }
  };

  renderDialogForm = (content, submitButton) => (
    <>
      <div className={Classes.DIALOG_BODY}>
        {content}
        {this.renderInstructions()}
      </div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  renderInstructions = () => {
    if (!this.state.isEditing) {
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

  refreshProblems = async () => {
    if (this.props.isOpen) {
      this.setState({ response: undefined });
      const response = await this.props.onGetProblems(this.props.problemSet.jid);
      this.setState({ response });
    }
  };

  toggleEditing = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  updateProblems = async data => {
    const problems = this.deserializeProblems(data.problems);
    await this.props.onSetProblems(this.props.problemSet.jid, problems);
    await this.refreshProblems();
    this.toggleEditing();
  };

  serializeProblems = (problems, problemsMap, contestsMap) => {
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

  deserializeProblems = problems => {
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

  validateProblems = value => {
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
}
