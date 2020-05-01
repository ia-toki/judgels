import { Callout, Classes, Button, Intent, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ChapterProblemsTable } from '../ChapterProblemsTable/ChapterProblemsTable';
import { Chapter } from '../../../../modules/api/jerahmeel/chapter';
import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import {
  ChapterProblemsResponse,
  ChapterProblem,
  ChapterProblemData,
} from '../../../../modules/api/jerahmeel/chapterProblem';
import ChapterProblemEditForm, { ChapterProblemEditFormData } from '../ChapterProblemEditForm/ChapterProblemEditForm';
import { Alias } from '../../../../components/forms/validations';

export interface ChapterProblemEditDialogProps {
  isOpen: boolean;
  chapter?: Chapter;
  onCloseDialog: () => void;
  onGetProblems: (chapterJid: string) => Promise<ChapterProblemsResponse>;
  onSetProblems: (chapterJid: string, data: ChapterProblemData[]) => Promise<void>;
}

interface ChapterProblemEditDialogState {
  response?: ChapterProblemsResponse;
  isEditing: boolean;
}

export class ChapterProblemEditDialog extends React.Component<
  ChapterProblemEditDialogProps,
  ChapterProblemEditDialogState
> {
  state: ChapterProblemEditDialogState = {
    isEditing: false,
  };

  componentDidMount() {
    this.refreshProblems();
  }

  async componentDidUpdate(prevProps: ChapterProblemEditDialogProps) {
    if (prevProps.chapter !== this.props.chapter) {
      this.refreshProblems();
    }
  }

  render() {
    const { isOpen } = this.props;
    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={this.closeDialog} title="Edit chapter problems" canOutsideClickClose={false}>
          {this.renderDialogContent()}
        </Dialog>
      </div>
    );
  }

  private closeDialog = () => {
    this.props.onCloseDialog();
    this.setState({ isEditing: false });
  };

  private renderDialogContent = () => {
    const { response, isEditing } = this.state;
    if (!response) {
      return this.renderDialogForm(<LoadingState />, null);
    }

    if (isEditing) {
      const props: any = {
        validator: this.validateProblems,
        renderFormComponents: this.renderDialogForm,
        onSubmit: this.updateProblems,
        initialValues: { problems: this.serializeProblems(response.data, response.problemsMap) },
      };
      return <ChapterProblemEditForm {...props} />;
    } else {
      const content = <ChapterProblemsTable response={response} />;
      const submitButton = <Button data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={this.toggleEditing} />;
      return this.renderDialogForm(content, submitButton);
    }
  };

  private renderDialogForm = (content: JSX.Element, submitButton: JSX.Element) => (
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

  private renderInstructions = () => {
    if (!this.state.isEditing) {
      return null;
    }

    return (
      <Callout icon={null}>
        <p>
          <strong>Format:</strong> <code>alias,slug[,type]</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,hello\nB,tree,PROGRAMMING\nC,flow,BUNDLE'}</pre>
      </Callout>
    );
  };

  private refreshProblems = async () => {
    if (this.props.isOpen) {
      const response = await this.props.onGetProblems(this.props.chapter.jid);
      this.setState({ response });
    }
  };

  private toggleEditing = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  private updateProblems = async (data: ChapterProblemEditFormData) => {
    const problems = this.deserializeProblems(data.problems);
    await this.props.onSetProblems(this.props.chapter.jid, problems);
    await this.refreshProblems();
    this.toggleEditing();
  };

  private serializeProblems = (problems: ChapterProblem[], problemsMap): string => {
    return problems
      .map(p => {
        if (p.type !== ProblemType.Programming) {
          return `${p.alias},${problemsMap[p.problemJid].slug},${p.type}`;
        } else {
          return `${p.alias},${problemsMap[p.problemJid].slug}`;
        }
      })
      .join('\n');
  };

  private deserializeProblems = (problems: string): ChapterProblemData[] => {
    return problems
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(
        s =>
          ({
            alias: s[0],
            slug: s[1],
            type: s[2] || ProblemType.Programming,
          } as ChapterProblemData)
      );
  };

  private validateProblems = (value: string) => {
    const problems = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases: string[] = [];
    const slugs: string[] = [];

    for (const p of problems) {
      if (p.length < 2 || p.length > 3) {
        return 'Each line must contain 2-3 comma-separated elements';
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
