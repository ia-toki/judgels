import { Callout, Classes, Button, Intent, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ChapterLessonsTable } from '../ChapterLessonsTable/ChapterLessonsTable';
import { Chapter } from '../../../../modules/api/jerahmeel/chapter';
import {
  ChapterLessonsResponse,
  ChapterLesson,
  ChapterLessonData,
} from '../../../../modules/api/jerahmeel/chapterLesson';
import ChapterLessonEditForm, { ChapterLessonEditFormData } from '../ChapterLessonEditForm/ChapterLessonEditForm';
import { Alias } from '../../../../components/forms/validations';

export interface ChapterLessonEditDialogProps {
  isOpen: boolean;
  chapter?: Chapter;
  onCloseDialog: () => void;
  onGetLessons: (chapterJid: string) => Promise<ChapterLessonsResponse>;
  onSetLessons: (chapterJid: string, data: ChapterLessonData[]) => Promise<void>;
}

interface ChapterLessonEditDialogState {
  response?: ChapterLessonsResponse;
  isEditing: boolean;
}

export class ChapterLessonEditDialog extends React.Component<
  ChapterLessonEditDialogProps,
  ChapterLessonEditDialogState
> {
  state: ChapterLessonEditDialogState = {
    isEditing: false,
  };

  componentDidMount() {
    this.refreshLessons();
  }

  async componentDidUpdate(prevProps: ChapterLessonEditDialogProps) {
    if (prevProps.chapter !== this.props.chapter) {
      this.refreshLessons();
    }
  }

  render() {
    const { isOpen } = this.props;
    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={this.closeDialog} title="Edit chapter lessons" canOutsideClickClose={false}>
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
        validator: this.validateLessons,
        renderFormComponents: this.renderDialogForm,
        onSubmit: this.updateLessons,
        initialValues: { lessons: this.serializeLessons(response.data, response.lessonsMap) },
      };
      return <ChapterLessonEditForm {...props} />;
    } else {
      const content = <ChapterLessonsTable response={response} />;
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
          <strong>Format:</strong> <code>alias,slug</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,hello\nB,tree'}</pre>
      </Callout>
    );
  };

  private refreshLessons = async () => {
    if (this.props.isOpen) {
      this.setState({ response: undefined });
      const response = await this.props.onGetLessons(this.props.chapter.jid);
      this.setState({ response });
    }
  };

  private toggleEditing = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  private updateLessons = async (data: ChapterLessonEditFormData) => {
    const lessons = this.deserializeLessons(data.lessons);
    await this.props.onSetLessons(this.props.chapter.jid, lessons);
    await this.refreshLessons();
    this.toggleEditing();
  };

  private serializeLessons = (lessons: ChapterLesson[], lessonsMap): string => {
    return lessons.map(c => `${c.alias},${lessonsMap[c.lessonJid].slug}`).join('\n');
  };

  private deserializeLessons = (lessons: string): ChapterLessonData[] => {
    return lessons
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
          } as ChapterLessonData)
      );
  };

  private validateLessons = (value: string) => {
    const lessons = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases: string[] = [];
    const slugs: string[] = [];

    for (const c of lessons) {
      if (c.length !== 2) {
        return 'Each line must contain 2 comma-separated elements';
      }
      const alias = c[0];
      const aliasValidation = Alias(alias);
      if (aliasValidation) {
        return 'Lesson aliases: ' + aliasValidation;
      }

      const slug = c[1];

      aliases.push(alias);
      slugs.push(slug);
    }

    if (new Set(aliases).size !== aliases.length) {
      return 'Lesson aliases must be unique';
    }
    if (new Set(slugs).size !== slugs.length) {
      return 'Lesson slugs must be unique';
    }

    return undefined;
  };
}
