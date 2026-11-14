import { Intent } from '@blueprintjs/core';
import { Flex } from '@blueprintjs/labs';

import { ButtonLink } from '../../../components/ButtonLink/ButtonLink';
import { FullPageLayout } from '../../../components/FullPageLayout/FullPageLayout';
import { HtmlText } from '../../../components/HtmlText/HtmlText';
import { useSession } from '../../../modules/session';
import { getHomeBanner, isRatingEnabled, isTrainingAccountRegistrationEnabled } from '../../../modules/webConfig';
import ActiveContestsWidget from '../widgets/activeContests/ActiveContestsWidget/ActiveContestsWidget';
import TopRatingsWidget from '../widgets/topRatings/TopRatingsWidget/TopRatingsWidget';

import './HomePage.scss';

export default function HomePage() {
  const { isLoggedIn } = useSession();

  const renderBanner = () => {
    if (isLoggedIn) {
      return null;
    }

    return (
      <div className="home-banner">
        <div className="home-banner__contents">
          <div className="home-banner__text">
            <HtmlText>{getHomeBanner() || ''}</HtmlText>
          </div>
          <Flex justifyContent="center" flexWrap="wrap" gap={2} marginTop={7}>
            {isTrainingAccountRegistrationEnabled() && (
              <ButtonLink to="/register" intent={Intent.PRIMARY} large>
                Register
              </ButtonLink>
            )}
            <ButtonLink to="/login" intent={Intent.NONE} large>
              Log in
            </ButtonLink>
          </Flex>
        </div>
      </div>
    );
  };

  const renderWidgets = () => {
    return (
      <div className="home-widget-row">
        <div className="home-widget-row__two-thirds">
          <ActiveContestsWidget />
        </div>
        <div className="home-widget-row__one-third">{isRatingEnabled() && <TopRatingsWidget />}</div>
      </div>
    );
  };

  return (
    <>
      {renderBanner()}
      <FullPageLayout>{renderWidgets()}</FullPageLayout>
    </>
  );
}
