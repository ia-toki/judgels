import { Component } from 'react';
import { connect } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import EditorialLanguageWidget from '../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorial } from '../../../../../../components/ProblemEditorial/ProblemEditorial';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { selectEditorialLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../modules/contestSelectors';

import * as contestEditorialActions from '../modules/contestEditorialActions';

class ContestEditorialPage extends Component {
  state = {
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  };

  async componentDidMount() {
    await this.refreshEditorial();
  }

  async componentDidUpdate(prevProps) {
    const { response } = this.state;
    if (this.props.editorialLanguage !== prevProps.editorialLanguage && response) {
      await this.refreshEditorial();
    }
  }

  refreshEditorial = async () => {
    const response = await this.props.onGetEditorial(this.props.contest.jid, this.props.editorialLanguage);
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(
      response.problemEditorialsMap,
      this.props.editorialLanguage
    );

    this.setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  };

  render() {
    return (
      <ContentCard>
        <h3>Editorial</h3>
        <hr />
        {this.renderEditorialLanguageWidget()}
        {this.renderEditorial()}
      </ContentCard>
    );
  }

  renderEditorialLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = this.state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props = {
      defaultLanguage,
      editorialLanguages: uniqueLanguages,
    };
    return <EditorialLanguageWidget {...props} />;
  };

  renderEditorial = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { preface, problems, problemsMap, problemEditorialsMap, problemMetadatasMap, profilesMap } = response;

    return (
      <div className="contest-editorial">
        {this.renderPreface(preface, profilesMap)}
        {problems
          .filter(p => problemMetadatasMap[p.problemJid].hasEditorial)
          .map(p =>
            this.renderProblemEditorial(
              p,
              problemsMap[p.problemJid],
              problemEditorialsMap[p.problemJid],
              problemMetadatasMap[p.problemJid],
              profilesMap
            )
          )}
      </div>
    );
  };

  renderPreface = (preface, profilesMap) => {
    if (!preface) {
      return null;
    }
    return (
      <ContentCard>
        <HtmlText profilesMap={profilesMap}>{preface}</HtmlText>
      </ContentCard>
    );
  };

  renderProblemEditorial = (problem, problemInfo, editorial, metadata, profilesMap) => {
    return (
      <ContentCard key={problem.problemJid + this.state.defaultLanguage}>
        <ProblemEditorial
          title={`${problem.alias}. ${getProblemName(problemInfo, this.state.defaultLanguage)}`}
          settersMap={metadata.settersMap}
          profilesMap={profilesMap}
        >
          {editorial.text}
        </ProblemEditorial>
      </ContentCard>
    );
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
  editorialLanguage: selectEditorialLanguage(state),
});

const mapDispatchToProps = {
  onGetEditorial: contestEditorialActions.getEditorial,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestEditorialPage);
