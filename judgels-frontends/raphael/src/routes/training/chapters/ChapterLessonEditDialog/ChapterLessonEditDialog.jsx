import { Callout, Classes, Button, Intent, Dialog } from '@blueprintjs/core';
import { Component } from 'react';

import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { ChapterLessonsTable } from '../ChapterLessonsTable/ChapterLessonsTable';
import ChapterLessonEditForm from '../ChapterLessonEditForm/ChapterLessonEditForm';
import { Alias } from '../../../../components/forms/validations';

export class ChapterLessonEditDialog extends Component {
  state = {
    response: undefined,
    isEditing: false,
  };

  componentDidMount() {
    this.refreshLessons();
  }

  async componentDidUpdate(prevProps) {
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
          <strong>Format:</strong> <code>alias,slug</code>
        </p>
        <p>
          <strong>Example:</strong>
        </p>
        <pre>{'A,hello\nB,tree'}</pre>
      </Callout>
    );
  };

  refreshLessons = async () => {
    if (this.props.isOpen) {
      this.setState({ response: undefined });
      const response = await this.props.onGetLessons(this.props.chapter.jid);
      this.setState({ response });
    }
  };

  toggleEditing = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };

  updateLessons = async data => {
    const lessons = this.deserializeLessons(data.lessons);
    await this.props.onSetLessons(this.props.chapter.jid, lessons);
    await this.refreshLessons();
    this.toggleEditing();
  };

  serializeLessons = (lessons, lessonsMap) => {
    return lessons.map(c => `${c.alias},${lessonsMap[c.lessonJid].slug}`).join('\n');
  };

  deserializeLessons = lessons => {
    return lessons
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()))
      .map(s => ({
        alias: s[0],
        slug: s[1],
      }));
  };

  validateLessons = value => {
    const lessons = value
      .split('\n')
      .map(s => s.trim())
      .filter(s => s.length > 0)
      .map(s => s.split(','))
      .map(s => s.map(t => t.trim()));

    const aliases = [];
    const slugs = [];

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
