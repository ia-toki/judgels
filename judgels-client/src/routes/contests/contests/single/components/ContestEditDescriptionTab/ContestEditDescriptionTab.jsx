import { Button, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectContest } from '../../../modules/contestSelectors';
import ContestEditDescriptionForm from '../ContestEditDescriptionForm/ContestEditDescriptionForm';

import * as contestActions from '../../../modules/contestActions';

class ContestEditDescriptionTab extends Component {
  state = {
    isEditing: false,
    response: undefined,
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
    const response = await this.props.onGetContestDescription(this.props.contest.jid);
    this.setState({ response });
  };

  renderEditButton = () => {
    return (
      !this.state.isEditing && (
        <Button small className="right-action-button" intent={Intent.PRIMARY} icon={<Edit />} onClick={this.toggleEdit}>
          Edit
        </Button>
      )
    );
  };

  renderContent = () => {
    const { isEditing, response } = this.state;
    if (response === undefined) {
      return <LoadingState />;
    }
    if (isEditing) {
      const initialValues = {
        description: response.description,
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
    return this.renderDescription(response);
  };

  renderDescription = ({ description, profilesMap }) => {
    if (!description) {
      return (
        <p>
          <small>No description.</small>
        </p>
      );
    }
    return (
      <ContentCard className="contest-edit-dialog__content">
        <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
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
