import { Intent, Card, Button, Classes, Dialog } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import EditorialLanguageWidget from '../../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { ProblemEditorial } from '../../../../../../../components/ProblemEditorial/ProblemEditorial';
import { selectProblemSet } from '../../../../modules/problemSetSelectors';
import { selectProblemSetProblem } from '../../modules/problemSetProblemSelectors';
import { selectEditorialLanguage } from '../../../../../../../modules/webPrefs/webPrefsSelectors';
import * as problemSetProblemActions from '../../modules/problemSetProblemActions';

import './ProblemEditorialDialog.css';

export class ProblemEditorialDialog extends Component {
  state = {
    isDialogOpen: false,
    response: undefined,
  };

  async componentDidUpdate(prevProps, prevState) {
    if (
      this.props.editorialLanguage !== prevProps.editorialLanguage &&
      (prevState.response || !prevState.isDialogOpen)
    ) {
      this.setState({ response: undefined });
    } else if (!this.state.response && (prevState.response || !prevState.isDialogOpen)) {
      const response = await this.props.onGetProblemEditorial(
        this.props.problemSet.jid,
        this.props.match.params.problemAlias,
        this.props.editorialLanguage
      );
      this.setState({ response });
    }
  }

  render() {
    return (
      <div>
        {this.renderButton()}
        {this.renderDialog()}
      </div>
    );
  }

  renderButton = () => {
    return (
      <Button
        className="problem-editorial-dialog-button"
        intent={Intent.WARNING}
        small
        onClick={this.toggleDialog}
        disabled={this.state.isDialogOpen}
      >
        View editorial
      </Button>
    );
  };

  renderDialog = () => {
    const { problemSet, problem } = this.props;
    const { isDialogOpen } = this.state;
    return (
      <Dialog className="problem-editorial-dialog" isOpen={isDialogOpen} onClose={this.toggleDialog} title="Editorial">
        <div className={Classes.DIALOG_BODY}>
          {this.renderEditorialLanguageWidget()}
          <Card className="problem-editorial-card">{this.renderEditorial()}</Card>
        </div>
      </Dialog>
    );
  };

  renderEditorial = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { editorial } = response;
    const { problemSet, problem, settersMap, profilesMap } = this.props;

    return (
      <ProblemEditorial
        title={`${problemSet.name} - Problem ${problem.alias}`}
        containerName={problemSet.name}
        settersMap={settersMap}
        profilesMap={profilesMap}
      >
        {editorial.text}
      </ProblemEditorial>
    );
  };

  toggleDialog = () => {
    this.setState(prevState => ({ isDialogOpen: !prevState.isDialogOpen }));
  };

  renderEditorialLanguageWidget = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    const { defaultLanguage, languages } = response.editorial;
    if (!defaultLanguage || !languages) {
      return null;
    }
    const props = {
      defaultLanguage,
      editorialLanguages: languages,
    };
    return (
      <div className="language-widget-wrapper">
        <EditorialLanguageWidget {...props} />
      </div>
    );
  };
}
const mapStateToProps = state => ({
  problemSet: selectProblemSet(state),
  problem: selectProblemSetProblem(state),
  editorialLanguage: selectEditorialLanguage(state),
});
const mapDispatchToProps = {
  onGetProblemEditorial: problemSetProblemActions.getProblemEditorial,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ProblemEditorialDialog));
