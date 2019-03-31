
import React, { Component } from 'react';
import { requireNativeComponent } from 'react-native';
import PropTypes from 'prop-types';

const MjpegPlayerView = requireNativeComponent(`MjpegPlayer`);

export default class RNMjpegplayer extends Component {
  render() {
    return <MjpegPlayerView {...this.props} />;
  }
}

RNMjpegplayer.propTypes = {
  settings: PropTypes.shape({}).isRequired,
};
