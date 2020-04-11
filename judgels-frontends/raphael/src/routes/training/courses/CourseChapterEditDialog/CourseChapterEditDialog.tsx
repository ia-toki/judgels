import { Classes, Button, Intent, Dialog } from '@blueprintjs/core';
import * as React from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { CourseChaptersTable } from '../CourseChaptersTable/CourseChaptersTable';
import { Course } from '../../../../modules/api/jerahmeel/course';
import { CourseChaptersResponse, CourseChapter } from '../../../../modules/api/jerahmeel/courseChapter';
import CourseChapterEditForm, { CourseChapterEditFormData } from '../CourseChapterEditForm/CourseChapterEditForm';
import { Alias } from '../../../../components/forms/validations';

export interface CourseChapterEditDialogProps {
  isOpen: boolean;
  course?: Course;
  onCloseDialog: () => void;
  onGetChapters: (courseJid: string) => Promise<CourseChaptersResponse>;
  onSetChapters: (courseJid: string, data: CourseChapter[]) => Promise<void>;
}

interface CourseChapterEditDialogState {
  response?: CourseChaptersResponse;
  isEditing: boolean;
}

export class CourseChapterEditDialog extends React.Component<
  CourseChapterEditDialogProps,
  CourseChapterEditDialogState
> {
  state: CourseChapterEditDialogState = {
    isEditing: false,
  };

  componentDidMount() {
    this.refreshChapters();
  }

  async componentDidUpdate(prevProps: CourseChapterEditDialogProps) {
    if (prevProps.course !== this.props.course) {
      this.refreshChapters();
    }
  }

  render() {
    const { isOpen } = this.props;
    return (
      <div className="content-card__section">
        <Dialog isOpen={isOpen} onClose={this.closeDialog} title="Edit course chapters" canOutsideClickClose={false}>
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
        validator: this.validateChapters,
        renderFormComponents: this.renderDialogForm,
        onSubmit: this.updateChapters,
        initialValues: { chapters: this.serializeChapters(response.data) },
      };
      return <CourseChapterEditForm {...props} />;
    } else {
      const content = <CourseChaptersTable response={response} />;
      const submitButton = <Button data-key="edit" text="Edit" intent={Intent.PRIMARY} onClick={this.toggleEditing} />;
      return this.renderDialogForm(content, submitButton);
    }
  };

  private renderDialogForm = (content: JSX.Element, submitButton: JSX.Element) => (
    <>
      <div className={Classes.DIALOG_BODY}>{content}</div>
      <div className={Classes.DIALOG_FOOTER}>
        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
          <Button text="Cancel" onClick={this.closeDialog} />
          {submitButton}
        </div>
      </div>
    </>
  );

  private refreshChapters = async () => {
    if (this.props.isOpen) {
      const response = await this.props.onGetChapters(this.props.course.jid);
      this.setState({ response });
    }
  };

  private toggleEditing = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  private updateChapters = async (data: CourseChapterEditFormData) => {
    const chapters = this.deserializeChapters(data.chapters);
    await this.props.onSetChapters(this.props.course.jid, chapters);
    await this.refreshChapters();
    this.toggleEditing();
  };

  private serializeChapters = (chapters: CourseChapter[]): string => {
    return chapters.map(c => `${c.alias},${c.chapterJid}`).join('\n');
  };

  private deserializeChapters = (chapters: string): CourseChapter[] => {
    return chapters
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(
        s =>
          ({
            alias: s[0],
            chapterJid: s[1],
          } as CourseChapter)
      );
  };

  private validateChapters = (value: string) => {
    const chapters = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases: string[] = [];
    const chapterJids: string[] = [];

    for (const c of chapters) {
      if (c.length !== 2) {
        return 'Each line must contain 2 comma-separated elements';
      }
      const alias = c[0];
      const aliasValidation = Alias(alias);
      if (aliasValidation) {
        return 'Chapter aliases: ' + aliasValidation;
      }

      const chapterJid = c[1];

      aliases.push(alias);
      chapterJids.push(chapterJid);
    }

    if (new Set(aliases).size !== aliases.length) {
      return 'Chapter aliases must be unique';
    }
    if (new Set(chapterJids).size !== chapterJids.length) {
      return 'Chapter JIDs must be unique';
    }

    return undefined;
  };
}
