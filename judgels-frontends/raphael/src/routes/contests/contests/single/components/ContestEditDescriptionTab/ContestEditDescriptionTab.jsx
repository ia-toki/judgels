import { Button, Intent } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';

import ContestEditDescriptionForm from '../ContestEditDescriptionForm/ContestEditDescriptionForm';
import { selectContest } from '../../../modules/contestSelectors';
import * as contestActions from '../../../modules/contestActions';

class ContestEditDescriptionTab extends Component {
  state = {
    isEditing: false,
    description: undefined,
  };

  async componentDidMount() {
    await this.refreshContestDescription();
  }

  render() {
    return (
      <>
        <h4>
          Description settings
          {this.renderEditButton()}
        </h4>
        <hr />
        {this.renderContent()}
      </>
    );
  }

  refreshContestDescription = async () => {
    const description = await this.props.onGetContestDescription(this.props.contest.jid);
    this.setState({ description });
  };

  renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon="edit" onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  renderContent = () => {
    const { isEditing, description } = this.state;
    if (description === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const initialValues = {
        description: description,
      };
      const formProps = {
        onCancel: this.toggleEdit,
      };
      return (
        <ContestEditDescriptionForm
          initialValues={initialValues}
          onSubmit={this.updateContestDescription}
          {...formProps}
        />
      );
    }
    return this.renderDescription(description);
  };

  renderDescription = description => {
    if (!description) {
      return (
        <p>
          <small>No description.</small>
        </p>
      );
    }
    return (
      <ContentCard className="contest-edit-dialog__content">
        <HtmlText>{description}</HtmlText>
      </ContentCard>
    );
  };

  updateContestDescription = async data => {
    await this.props.onUpdateContestDescription(this.props.contest.jid, data.description);
    await this.refreshContestDescription();
    this.toggleEdit();
  };

  toggleEdit = () => {
    this.setState(prevState => ({
      isEditing: !prevState.isEditing,
    }));
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});
const mapDispatchToProps = {
  onGetContestDescription: contestActions.getContestDescription,
  onUpdateContestDescription: contestActions.updateContestDescription,
};
export default connect(mapStateToProps, mapDispatchToProps)(ContestEditDescriptionTab);
