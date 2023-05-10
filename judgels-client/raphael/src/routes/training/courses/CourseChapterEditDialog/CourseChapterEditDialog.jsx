import { Callout, Classes, Button, Intent, Dialog } from '@blueprintjs/core';
import { Component } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { CourseChaptersTable } from '../CourseChaptersTable/CourseChaptersTable';
import CourseChapterEditForm from '../CourseChapterEditForm/CourseChapterEditForm';
import { Alias } from '../../../../components/forms/validations';

export class CourseChapterEditDialog extends Component {
  state = {
    response: undefined,
    isEditing: false,
  };

  componentDidMount() {
    this.refreshChapters();
  }

  async componentDidUpdate(prevProps) {
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
          <strong>Format:</strong> <code>alias,chapterJid</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,JIDSESS123\nB,JIDSESS456'}</pre>
      </Callout>
    );
  };

  refreshChapters = async () => {
    if (this.props.isOpen) {
      this.setState({ response: undefined });
      const response = await this.props.onGetChapters(this.props.course.jid);
      this.setState({ response });
    }
  };

  toggleEditing = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  updateChapters = async data => {
    const chapters = this.deserializeChapters(data.chapters);
    await this.props.onSetChapters(this.props.course.jid, chapters);
    await this.refreshChapters();
    this.toggleEditing();
  };

  serializeChapters = chapters => {
    return chapters.map(c => `${c.alias},${c.chapterJid}`).join('\n');
  };

  deserializeChapters = chapters => {
    return chapters
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(s => ({
        alias: s[0],
        chapterJid: s[1],
      }));
  };

  validateChapters = value => {
    const chapters = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases = [];
    const chapterJids = [];

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
