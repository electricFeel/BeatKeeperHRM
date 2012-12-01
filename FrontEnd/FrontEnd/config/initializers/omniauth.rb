Rails.application.config.middleware.use OmniAuth::Builder do
  provider :facebook, '115624008457102', 'b8dc5b7ef009d308132fc1e9de4e102e'
end