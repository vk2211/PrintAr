#ifndef __EASYAR_SAMPLE_AR_VIDEO_H__
#define __EASYAR_SAMPLE_AR_VIDEO_H__

namespace EasyAR {
	namespace samples {


class HelloARVideo : public AR {
public:
	HelloARVideo();

	~HelloARVideo();

	void push(VideoData v);

	void init();

	virtual void initGL();

	void addGL();

	virtual void resizeGL(int width, int height);

	virtual void render();

	virtual bool clear();

	const VideoData getCurrentVideoData() const
	{
		if (m_pVideoData != NULL)
		{
			VideoData videoData(*m_pVideoData);
			videoData.setStartTime(video->getPos());
			return videoData;
		}
		else
		{
			VideoData videoData;
			videoData.setVideoPath("");
			videoData.setImagePath("");
			return videoData;
		}
	}
private:
	Vec2I view_size;
	std::vector<VideoRenderer *> renderer;
	int tracked_target;
	int active_target;
	std::vector<int> texid;
	ARVideo *video;
	VideoRenderer *video_renderer;
	std::vector<VideoData> m_vVideoData;
	VideoData *m_pVideoData;
};

	}
}

#endif
